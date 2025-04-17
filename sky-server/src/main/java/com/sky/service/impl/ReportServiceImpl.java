package com.sky.service.impl;

import com.sky.constraint.DateCountable;
import com.sky.entity.*;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    UserMapper userMapper;

    /**
     * 营业额统计
     * @param beginDate
     * @param endDate
     * @return
     */
    public TurnoverReportVO turnoverStatistics(LocalDate beginDate, LocalDate endDate) {
        if (beginDate == null || endDate == null) {
            throw new IllegalArgumentException("日期参数不能为空");
        }

        List<String> dateRange = generateDateRange(beginDate, endDate); // 日期序列生成独立

        Map<String, BigDecimal> dailyTurnover = calculateDailyAmount(
                orderMapper.getMoneyAndDate(beginDate, endDate.plusDays(1),5) // 查询
        );

        return buildResult(dateRange, dailyTurnover); // 结果构建独立
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO userStatistics(LocalDate begin, LocalDate end){
        String dateList = generateDateRange(begin, end).stream()
                .collect(Collectors.joining(","));

        List<LocalDate> allDates = new ArrayList<>();
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            allDates.add(date);
        }

        //记录begin->end时间内增加的用户数
        List<UserCountByDate> newUsers = userMapper.getByDate(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        String newUsersString = convertToString(allDates,newUsers);

        //获取总的时间内的增加的用户数
        List<UserCountByDate> totalUsers = userMapper.get();

        Map<LocalDate, Integer> userList = totalUsers
                .stream()
                .collect(Collectors.toMap(UserCountByDate::getDate, UserCountByDate::getCount));

        // 使用原子整型实现线程安全累加
        AtomicInteger accumulator = new AtomicInteger(0);

        String totalUsersString = allDates.stream()
                .map(date -> {
                    int dailyCount = userList.getOrDefault(date, 0);;
                    int currentTotal = accumulator.addAndGet(dailyCount);
                    return String.valueOf(currentTotal);
                })
                .collect(Collectors.joining(","));

        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(dateList)
                .totalUserList(totalUsersString)
                .newUserList(newUsersString)
                .build();

        return userReportVO;
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end){
        String dateList = generateDateRange(begin, end).stream()
                .collect(Collectors.joining(","));

        //根据开始和结束时间获取每天有效订单数
        List<OrderCountByDate> validOrderCountList = orderMapper.getValidOrderCountList(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        //根据开始和结束时间获取每天总订单数
        List<OrderCountByDate> orderCountList = orderMapper.getTotalOrderCountList(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        // 订单列表String化
        List<LocalDate> allDates = new ArrayList<>();
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            allDates.add(date);
        }
        String validOrderCountString = convertToString(allDates, validOrderCountList);
        String orderCountString = convertToString(allDates, orderCountList);

        //订单完成率
        Double orderCompletionRate = getOrderCompletionRate(validOrderCountList,orderCountList);

        //有效订单数和订单总数
        int orderCount = orderCountList.stream()
                .map(dto -> dto.getCount() != null ? dto.getCount() : 0)
                .mapToInt(Integer::intValue)
                .sum();
        int validOrderCount = validOrderCountList.stream()
                .map(dto -> dto.getCount() != null ? dto.getCount() : 0)
                .mapToInt(Integer::intValue)
                .sum();

        OrderReportVO orderReportVO = OrderReportVO.builder()
                .totalOrderCount(orderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .dateList(dateList)
                .validOrderCountList(validOrderCountString)
                .orderCountList(orderCountString)
                .build();

        return orderReportVO;
    }

    /**
     * 查询销量排行top10
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end){
        //根据开始时间和结束时间获取商品销量
        List<SalesCountByName> salesCountByNames = orderMapper.getSalesCountList(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();

        int i = 0;
        for(SalesCountByName s : salesCountByNames){
            if(i == 10) break;
            nameList.add(s.getName());
            numberList.add(s.getCount());
        }

        String nameString = nameList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String numberString = numberList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return new SalesTop10ReportVO(nameString,numberString);
    }

    // 日期解析（异常处理可扩展）
    private LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    // 生成日期序列（时间复杂度O(n)）
    private List<String> generateDateRange(LocalDate begin, LocalDate end) {
        List<String> dates = new ArrayList<>();
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dates.add(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        return dates;
    }

    // 按日聚合金额（时间复杂度O(m)，m为订单数）
    private Map<String, BigDecimal> calculateDailyAmount(List<OrderMoneyAndDate> orders) {
        return orders.stream()
                .filter(omd -> omd != null && omd.getOrderTime() != null) // 空值过滤
                .collect(Collectors.groupingBy(
                        omd -> omd.getOrderTime().toLocalDate().toString(),
                        Collectors.reducing(BigDecimal.ZERO, OrderMoneyAndDate::getAmount, BigDecimal::add)
                ));
    }

    // 构建结果（流式处理优化内存）
    private TurnoverReportVO buildResult(List<String> dateRange, Map<String, BigDecimal> dailyTurnover) {
        String dates = String.join(",", dateRange);
        String amounts = dateRange.stream()
                .map(date -> dailyTurnover.getOrDefault(date, BigDecimal.ZERO).toString())
                .collect(Collectors.joining(","));
        return new TurnoverReportVO(dates, amounts);
    }


    //根据订单总数和有效订单数计算订单完成率
    public Double getOrderCompletionRate(List<OrderCountByDate> validOrders,List<OrderCountByDate> totalOrders) {
        // 校验数据有效性
        if (validOrders.size() == 0) {
            return 0.0;
        }

        // 聚合求和
        int validSum = validOrders.stream()
                .map(OrderCountByDate::getCount)
                .mapToInt(Integer::intValue)
                .sum();

        int totalSum = totalOrders.stream()
                .map(OrderCountByDate::getCount)
                .mapToInt(Integer::intValue)
                .sum();

        // 避免除零异常
        if (totalSum == 0) {
            return 0.0;
        }

        // 计算完成率并保留两位小数
        double rate = validSum * 100.000 / totalSum;
        return Math.round(rate * 100.0) / 10000.0;
    }

    private <T extends DateCountable> String convertToString(List<LocalDate> allDates, List<T> list){
        // 转换为日期->数量的Map（存在则取值，不存在则默认0）
        Map<LocalDate, Integer> map = list.stream()
                .collect(Collectors.toMap(T::getDate, T::getCount));
        // 按日期顺序填充数据
        String string = allDates.stream()
                .map(date -> String.valueOf(map.getOrDefault(date, 0)))
                .collect(Collectors.joining(","));

        return string;
    }

    /**
     * 批量创建用户
     */
    private void insertBatchUsers(){
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_+=/";
        List<User> users = new ArrayList<>();
        for(LocalDateTime date = LocalDateTime.now().plusDays(-15); !date.isAfter(LocalDateTime.now().plusDays(15)); date = date.plusDays(1)){
            long seed = date.toEpochSecond(ZoneOffset.UTC);
            Random random = new Random(seed);
            int number = random.nextInt(300) + 1;
            for(int i = 0; i < number; i++){
                User user = new User();
                StringBuilder sb = new StringBuilder(27);
                for (int j = 0; j < 27; j++) {
                    int index = ThreadLocalRandom.current().nextInt(CHARACTERS.length());
                    sb.append(CHARACTERS.charAt(index));
                }
                user.setOpenid(String.valueOf(sb));
                user.setCreateTime(date);
                users.add(user);
            }
        }
        userMapper.insertBatch(users);
    }
}

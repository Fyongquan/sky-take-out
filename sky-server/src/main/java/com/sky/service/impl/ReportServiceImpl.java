package com.sky.service.impl;

import com.sky.entity.OrderMoneyAndDate;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO turnoverStatistics(String begin, String end){

        if (begin == null || end == null) {
            throw new IllegalArgumentException("日期参数不能为空");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //解析字符串为LocalDate
        LocalDate beginDate = LocalDate.parse(begin,formatter);
        LocalDate endDate = LocalDate.parse(end,formatter);

        LocalDate nextEndDate = endDate.plusDays(1);
        //解析回String
        String newEnd = nextEndDate.format(formatter);

        List<OrderMoneyAndDate> orderMoneyAndDates = orderMapper.getMoneyAndDate(begin, newEnd);

        //创建日期List和营业额List还有中间DateMap(标注日期在营业额List中的下标)
        Map<String, Integer> dateMap = new HashMap<>();
        List<String> dateList = new ArrayList<>();
        List<BigDecimal> turnoverList = new ArrayList<>();
        LocalDate currentDate = beginDate;
        int i = 0;
        while(!currentDate.equals(nextEndDate)){
            String currentDateString = currentDate.format(formatter);
            dateList.add(currentDateString);
            dateMap.put(currentDateString,i++);
            turnoverList.add(BigDecimal.ZERO);
            currentDate = currentDate.plusDays(1);
        }

        for(OrderMoneyAndDate omd : orderMoneyAndDates){
            String omdDate = omd.getOrderTime().toLocalDate().format(formatter);
            BigDecimal amount = turnoverList.get((Integer) dateMap.get(omdDate)).add(omd.getAmount());
            if(amount != null){
                turnoverList.set((Integer) dateMap.get(omdDate),amount);
            }
        }

        String dateList1 = String.join(",",dateList);
        String turnoverList1 = turnoverList.stream()
                .map(BigDecimal::toString)
                .collect(Collectors.joining(","));

        return new TurnoverReportVO(dateList1,turnoverList1);
    }
}

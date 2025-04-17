package com.sky.service.impl;

import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 查询今日运营数据
     * @return
     */
    public BusinessDataVO businessData(){

        LocalDateTime begin = LocalDateTime.of(LocalDate.now(),LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        //新增用户数
        Integer newUsers = userMapper.getUsersCount(begin, end);
        //营业额
        Double turnover = orderMapper.getTurnover(begin, end);
        //有效订单数
        Integer validOrderCount = orderMapper.getValidOrderCount(begin, end);
        //全部订单数
        Integer orderCount = orderMapper.getOrderCount(begin, end);
        //订单完成率
        Double orderCompletionRate = division(validOrderCount,orderCount);
        //平均客单价
        Double unitPrice = division(turnover, validOrderCount);

        BusinessDataVO businessDataVO = BusinessDataVO.builder()
                .newUsers(newUsers)
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .build();

        return businessDataVO;
    }

    /**
     * 查询订单管理数据
     * @return
     */
    public OrderOverViewVO overviewOrders(){
        LocalDateTime begin = LocalDateTime.of(LocalDate.now(),LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        //全部订单
        Integer allOrders = orderMapper.getOrderCount(begin, end);
        //已取消数量
        Integer cancelledOrders = orderMapper.getCancelledOrders(begin, end);
        //已完成数量
        Integer completedOrders = orderMapper.getValidOrderCount(begin, end);
        //待派送数量
        Integer deliveredOrders = orderMapper.getDeliveredOrders(begin, end);
        //待接单数量
        Integer waitingOrders = orderMapper.getWaitingOrders(begin, end);

        OrderOverViewVO orderOverViewVO = OrderOverViewVO.builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders)
                .build();

        return orderOverViewVO;
    }

    /**
     * 查询菜品总览
     * @return
     */
    public DishOverViewVO overviewDishes(){

        //已停售菜品数量
        Integer discontinued = dishMapper.getCountByStatus(0);
        //已启售菜品数量
        Integer sold = dishMapper.getCountByStatus(1);

        return new DishOverViewVO(sold, discontinued);
    }

    /**
     * 查询套餐总览
     * @return
     */
    public SetmealOverViewVO overviewSetmeals(){

        //已停售套餐数量
        Integer discontinued = setmealMapper.getCountByStatus(0);
        //已启售套餐数量
        Integer sold = setmealMapper.getCountByStatus(1);

        return new SetmealOverViewVO(sold, discontinued);
    }


    //计算除法
    private <T> Double division(Number num1, Number num2){
        if(num2.doubleValue() == 0.0){
            return 0.0;
        }

        double value1 = num1.doubleValue();
        double value2 = num2.doubleValue();

        Double rate = value1 * 100.00 / value2;
        return Math.round(rate * 100.0) / 10000.0;
    }
}

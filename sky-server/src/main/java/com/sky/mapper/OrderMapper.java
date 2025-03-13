package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderMoneyAndDate;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 新增订单
     * @param orders
     */
    Long insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 分页查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单号查询订单
     * @param id
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 获取某个状态的数量
     * @return
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer getStatusCount(Integer status);

    /**
     * 根据订单状态和下单时间查询订单
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getBystatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 根据订单id获取订单号
     * @param id
     * @return
     */
    @Select("select number from orders where id = #{id}")
    String getNumberById(Long id);

    /**
     * 根据开始和结束时间获取订单的营业额和下单时间
     * @param begin
     * @param end
     * @return
     */
    @Select("select amount, order_time from orders where order_time >= #{begin}  and order_time < #{end}")
    List<OrderMoneyAndDate> getMoneyAndDate(String begin, String end);
}

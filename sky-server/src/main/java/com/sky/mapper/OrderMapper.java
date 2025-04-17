package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderCountByDate;
import com.sky.entity.OrderMoneyAndDate;
import com.sky.entity.Orders;
import com.sky.entity.SalesCountByName;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
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
    @Select("select amount, order_time from orders where order_time >= #{begin}  and order_time < #{end} and status = #{status}")
    List<OrderMoneyAndDate> getMoneyAndDate(LocalDate begin, LocalDate end, Integer status);

    /**
     * 根据开始和结束时间获取每天有效订单数
     * @param begin
     * @param end
     * @return
     */
    @Select("select DATE(order_time) as date, COUNT(*) as count from orders " +
            "where order_time >= #{begin} and order_time < #{end} and status = 5 " +
            "group by date order by date asc")
    List<OrderCountByDate> getValidOrderCountList(LocalDateTime begin, LocalDateTime end);

    /**
     * 根据开始和结束时间获取每天总订单数
     * @param begin
     * @param end
     * @return
     */
    @Select("select DATE(order_time) as date, COUNT(*) as count from orders " +
            "where order_time >= #{begin} and order_time < #{end} " +
            "group by date order by date asc")
    List<OrderCountByDate> getTotalOrderCountList(LocalDateTime begin, LocalDateTime end);

    /**
     * 根据开始时间和结束时间获取商品销量
     * @param begin
     * @param end
     * @return
     */
    @Select("select od.name as name, SUM(od.number) as count from order_detail as od left join orders as o on od.order_id = o.id where o.order_time >= #{begin} and o.order_time < #{end} and o.status = 5 " +
            "group by od.name ORDER BY count DESC;")
    List<SalesCountByName> getSalesCountList(LocalDateTime begin, LocalDateTime end);

    /**
     * 根据开始时间和结束时间获取营业额
     * @param begin
     * @param end
     * @return
     */
    @Select("select SUM(amount) from orders where order_time >= #{begin} and orders.order_time < #{end} and status = 5")
    Double getTurnover(LocalDateTime begin, LocalDateTime end);

    /**
     * 根据开始时间和结束时间获取有效订单数
     * @param begin
     * @param end
     * @return
     */
    @Select("select COUNT(id) from orders where order_time >= #{begin} and orders.order_time < #{end} and status = 5")
    Integer getValidOrderCount(LocalDateTime begin, LocalDateTime end);

    /**
     * 根据开始时间和结束时间获取全部订单数
     * @param begin
     * @param end
     * @return
     */
    @Select("select COUNT(id) from orders where order_time >= #{begin} and orders.order_time < #{end}")
    Integer getOrderCount(LocalDateTime begin, LocalDateTime end);

    /**
     * 根据开始时间和结束时间获取取消订单数
     * @param begin
     * @param end
     * @return
     */
    @Select("select COUNT(id) from orders where order_time >= #{begin} and orders.order_time < #{end} and status = 6")
    Integer getCancelledOrders(LocalDateTime begin, LocalDateTime end);

    /**
     * 根据开始时间和结束时间获取待派送订单数
     * @param begin
     * @param end
     * @return
     */
    @Select("select COUNT(id) from orders where order_time >= #{begin} and orders.order_time < #{end} and status = 3")
    Integer getDeliveredOrders(LocalDateTime begin, LocalDateTime end);

    /**
     * 根据开始时间和结束时间获取待派送订单数
     * @param begin
     * @param end
     * @return
     */
    @Select("select COUNT(id) from orders where order_time >= #{begin} and orders.order_time < #{end} and status = 2")
    Integer getWaitingOrders(LocalDateTime begin, LocalDateTime end);
}



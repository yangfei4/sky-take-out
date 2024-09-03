package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 创建订单
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 按照付款状态与订单时间查询订单
     * @param paymentStatus
     * @param orderTime
     * @return
     */
    @Select("select * from orders where status = #{paymentStatus} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer paymentStatus, LocalDateTime orderTime);

    /**
     * 更新订单
     * @param order
     */
    void update(Orders order);
}

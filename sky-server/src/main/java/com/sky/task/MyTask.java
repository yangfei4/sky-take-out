package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class MyTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 每分钟检查并处理支付超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrders() {
        log.info("正在处理支付超时订单, {}", LocalDateTime.now());

        List<Orders> list = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, LocalDateTime.now().minusMinutes(15));

        for (Orders order : list) {
            order.setStatus(Orders.CANCELLED);
            order.setCancelReason("支付超时，自动取消");
            order.setCancelTime(LocalDateTime.now());
            orderMapper.update(order);
        }
    }

    /**
     * 凌晨一点处理派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDelieverdOrder() {
        log.info("定时处理派送中订单，{}", LocalDateTime.now());

        List<Orders> list = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().minusMinutes(60));

        for (Orders order : list) {
            order.setStatus(Orders.COMPLETED);
            orderMapper.update(order);
        }
    }

}

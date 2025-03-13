package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMoneyAndDate implements Serializable {

    //单个订单的营业额
    private BigDecimal amount;

    //订单的下单时间
    private LocalDateTime orderTime;
}

package com.sky.entity;

import com.sky.constraint.DateCountable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCountByDate implements Serializable, DateCountable {

    //天
    private LocalDate date;

    //每天的订单数
    private Integer count;
}

package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.sky.constraint.DateCountable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCountByDate implements Serializable, DateCountable {

    //天
    private LocalDate date;

    //每天的用户数
    private Integer count;
}

package com.lordeats.csmobeats.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostReservation {
    private String place;
    private BigDecimal price;
    private int customer_id;
}
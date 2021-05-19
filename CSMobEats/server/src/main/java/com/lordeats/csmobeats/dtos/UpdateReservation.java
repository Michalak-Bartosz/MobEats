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
public class UpdateReservation {
    private int id;
    private String place;
    private BigDecimal price;
}

package com.lordeats.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetReservation {
    private int id;
    private String name;
    private String address;
    private String fonNumber;
    private String emailAddress;
    private String ratingPoints;
    private String webPage;
    private BigDecimal price;
    private int customer_id;
}

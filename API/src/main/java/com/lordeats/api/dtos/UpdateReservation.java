package com.lordeats.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservation {
    private String name;
    private String address;
    private String priceLevel;
    private String fonNumber;
    private String ratingPoints;
    private String webPage;
}

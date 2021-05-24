package com.lordeats.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomer {
    private int id;
    private String nickname;
    private String password;
    private Set<Integer> reservations_id;
}

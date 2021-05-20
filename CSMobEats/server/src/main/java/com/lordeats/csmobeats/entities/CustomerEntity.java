package com.lordeats.csmobeats.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="customer")
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="nickname", nullable = false)
    private String nickname;

    @Column(name="password", nullable = false)
    private String password;

    @OneToMany(mappedBy="customer")
    private Set<ReservationEntity> reservations;

    public CustomerEntity(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public Set<Integer> getReservationsId() {
        Set<Integer> idList = new HashSet<>();
        for(ReservationEntity reservation: reservations){
            idList.add(reservation.getId());
        }
        return idList;
    }
}
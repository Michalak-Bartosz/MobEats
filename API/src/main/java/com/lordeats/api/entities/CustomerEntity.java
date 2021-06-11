package com.lordeats.api.entities;

import com.lordeats.api.dtos.GetReservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @OneToMany(mappedBy="customer", fetch = FetchType.EAGER)
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

    public boolean existsReservation(String name) {
        for(ReservationEntity reservation: reservations){
            if(reservation.getName().equals(name))
                return true;
        }
        return false;
    }

    public String getReservationsString() {
        Set<JSONObject> JSONList = new HashSet<>();
        for(ReservationEntity reservation: reservations){
            JSONList.add(reservation.reservationToJsonObject());
        }
        return JSONList.toString();
    }

    public List<GetReservation> getReservations() {
        List<GetReservation>  reservationsList = new ArrayList<>();
        for(ReservationEntity reservation: reservations){
            reservationsList.add(reservation.reservationToGetReservationObject());
        }
        return reservationsList;
    }

    public boolean hasReservations() {
        return !reservations.isEmpty();
    }
}
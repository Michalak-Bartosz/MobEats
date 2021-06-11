package com.lordeats.api.services;

import com.lordeats.api.dtos.GetReservation;
import com.lordeats.api.dtos.PostReservation;
import com.lordeats.api.dtos.UpdateReservation;

import java.util.List;

public interface ReservationService {
    List<GetReservation> getAllReservations();

    List<GetReservation> getCustomerReservations(String nickname);

    GetReservation getReservation(int id);

    boolean addNewReservation(PostReservation request);

    boolean updateReservation(int reservationId, UpdateReservation updateReservation);

    boolean deleteReservation(int id);

    boolean deleteAllReservations();
}

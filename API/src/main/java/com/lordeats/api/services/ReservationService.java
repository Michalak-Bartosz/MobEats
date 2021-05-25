package com.lordeats.api.services;

import com.lordeats.api.dtos.GetReservation;
import com.lordeats.api.dtos.PostReservation;
import com.lordeats.api.dtos.UpdateReservation;

import java.util.List;

public interface ReservationService {
    List<GetReservation> getAllReservations();

    boolean addNewReservation(PostReservation request);

    boolean updateReservation(UpdateReservation updateReservation);

    boolean deleteReservation(int id);

    boolean deleteAllReservations();
}

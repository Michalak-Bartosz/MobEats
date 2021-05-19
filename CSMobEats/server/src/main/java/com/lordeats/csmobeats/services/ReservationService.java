package com.lordeats.csmobeats.services;

import com.lordeats.csmobeats.dtos.GetReservation;
import com.lordeats.csmobeats.dtos.PostReservation;
import com.lordeats.csmobeats.dtos.UpdateReservation;

import java.util.List;

public interface ReservationService {
    List<GetReservation> getAllReservations();

    boolean addNewReservation(PostReservation request);

    boolean updateReservation(UpdateReservation updateReservation);

    boolean deleteReservation(int id);
}

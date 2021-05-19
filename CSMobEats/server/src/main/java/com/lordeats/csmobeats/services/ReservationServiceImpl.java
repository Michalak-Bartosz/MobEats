package com.lordeats.csmobeats.services;

import com.lordeats.csmobeats.dtos.GetReservation;
import com.lordeats.csmobeats.dtos.PostReservation;
import com.lordeats.csmobeats.dtos.UpdateReservation;
import com.lordeats.csmobeats.entities.ReservationEntity;
import com.lordeats.csmobeats.repositories.CustomerRepository;
import com.lordeats.csmobeats.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ReservationServiceImpl implements ReservationService{

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, CustomerRepository customerRepository) {
        this.reservationRepository = reservationRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<GetReservation> getAllReservations() {
        return StreamSupport.stream(reservationRepository.findAll().spliterator(), false).map(reservationEntity -> new GetReservation(reservationEntity.getId(),reservationEntity.getPlace(),reservationEntity.getPrice(),reservationEntity.getCustomer().getId())).collect(Collectors.toList());
    }

    @Override
    public boolean addNewReservation(PostReservation postReservation) {
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setPlace(postReservation.getPlace());
        reservationEntity.setPrice(postReservation.getPrice());
        reservationEntity.setCustomer(customerRepository.findById(postReservation.getCustomer_id()));

        reservationRepository.save(reservationEntity);
        return reservationRepository.existsById(reservationEntity.getId());
    }

    @Override
    public boolean updateReservation(UpdateReservation updateReservation) {
        if(reservationRepository.existsById(updateReservation.getId())) {
            ReservationEntity reservation = reservationRepository.findById(updateReservation.getId());
            reservation.setPlace(updateReservation.getPlace());
            reservation.setPrice(updateReservation.getPrice());
            reservationRepository.save(reservation);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteReservation(int id) {
        if(reservationRepository.existsById(id)){
            reservationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

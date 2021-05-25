package com.lordeats.api.services;

import com.lordeats.api.dtos.GetReservation;
import com.lordeats.api.dtos.PostReservation;
import com.lordeats.api.dtos.UpdateReservation;
import com.lordeats.api.entities.ReservationEntity;
import com.lordeats.api.repositories.CustomerRepository;
import com.lordeats.api.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        return StreamSupport.stream(reservationRepository.findAll().spliterator(), false).map(
                reservationEntity -> new GetReservation(reservationEntity.getId(), reservationEntity.getName(),
                        reservationEntity.getAddress(), reservationEntity.getFonNumber(), reservationEntity.getEmailAddress(),
                        reservationEntity.getRatingPoints(), reservationEntity.getWebPage(),reservationEntity.getPrice(),
                        reservationEntity.getCustomer().getId())).collect(Collectors.toList());
    }

    @Override
    public GetReservation getReservation(int id) {
        ReservationEntity reservationEntity = reservationRepository.findById(id);
        if(reservationEntity != null)
            return new GetReservation(reservationEntity.getId(), reservationEntity.getName(),
                reservationEntity.getAddress(), reservationEntity.getFonNumber(), reservationEntity.getEmailAddress(),
                reservationEntity.getRatingPoints(), reservationEntity.getWebPage(),reservationEntity.getPrice(),
                reservationEntity.getCustomer().getId());
        return null;
    }

    private void setNewReservation(ReservationEntity reservationEntity, String name, String address, int fonNumber, String emailAddress, String ratingPoints, String webPage, BigDecimal price) {
        reservationEntity.setName(name);
        reservationEntity.setAddress(address);
        reservationEntity.setFonNumber(fonNumber);
        reservationEntity.setEmailAddress(emailAddress);
        reservationEntity.setRatingPoints(ratingPoints);
        reservationEntity.setWebPage(webPage);
        reservationEntity.setPrice(price);
    }

    @Override
    public boolean addNewReservation(PostReservation postReservation) {
        if(!customerRepository.existsById(postReservation.getCustomer_id()))
            return false;
        ReservationEntity reservationEntity = new ReservationEntity();
        setNewReservation(reservationEntity, postReservation.getName(), postReservation.getAddress(),
                postReservation.getFonNumber(), postReservation.getEmailAddress(), postReservation.getRatingPoints(),
                postReservation.getWebPage(), postReservation.getPrice());

        reservationEntity.setCustomer(customerRepository.findById(postReservation.getCustomer_id()));
        reservationRepository.save(reservationEntity);
        return reservationRepository.existsById(reservationEntity.getId());
    }



    @Override
    public boolean updateReservation(UpdateReservation updateReservation) {
        if(reservationRepository.existsById(updateReservation.getId())) {
            ReservationEntity reservationEntity = reservationRepository.findById(updateReservation.getId());
            setNewReservation(reservationEntity, updateReservation.getName(), updateReservation.getAddress(),
                    updateReservation.getFonNumber(), updateReservation.getEmailAddress(), updateReservation.getRatingPoints(),
                    updateReservation.getWebPage(), updateReservation.getPrice());

            reservationRepository.save(reservationEntity);
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

    @Override
    public boolean deleteAllReservations() {
        Iterable<ReservationEntity> reservationList = reservationRepository.findAll();
        for(ReservationEntity reservation: reservationList){
            reservationRepository.delete(reservation);
        }
        return reservationRepository.count() == 0;
    }
}

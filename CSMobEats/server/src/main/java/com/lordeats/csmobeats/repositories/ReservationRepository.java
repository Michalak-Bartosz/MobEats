package com.lordeats.csmobeats.repositories;

import com.lordeats.csmobeats.entities.CustomerEntity;
import com.lordeats.csmobeats.entities.ReservationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReservationRepository extends CrudRepository<ReservationEntity, Integer> {
    List<ReservationEntity> findByCustomer(CustomerEntity customer, Sort sort);
    ReservationEntity findById(int id);
}

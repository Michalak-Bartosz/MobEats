package com.lordeats.api.repositories;

import com.lordeats.api.entities.CustomerEntity;
import com.lordeats.api.entities.ReservationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<ReservationEntity, Integer> {
    List<ReservationEntity> findByCustomer(CustomerEntity customer, Sort sort);
    ReservationEntity findById(int id);
}

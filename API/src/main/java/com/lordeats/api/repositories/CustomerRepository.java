package com.lordeats.api.repositories;


import com.lordeats.api.entities.CustomerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, Integer> {
    CustomerEntity findById(int id);
    CustomerEntity findByNickname(String nickname);
    boolean existsByNickname(String nickname);
}


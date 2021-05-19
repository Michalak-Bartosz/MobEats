package com.lordeats.csmobeats.repositories;


import com.lordeats.csmobeats.entities.CustomerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, Integer> {
    CustomerEntity findById(int id);
    CustomerEntity findByNickname(String nickname);
    boolean existsByNickname(String nickname);
}


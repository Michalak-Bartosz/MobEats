package com.lordeats.csmobeats;

import com.lordeats.csmobeats.entities.CustomerEntity;
import com.lordeats.csmobeats.entities.ReservationEntity;
import com.lordeats.csmobeats.repositories.CustomerRepository;
import com.lordeats.csmobeats.repositories.ReservationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class ServerMobEatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerMobEatsApplication.class, args);
    }

    //Wprowadzanie danych testowych
//    @Bean
//    public CommandLineRunner mappingDemo(CustomerRepository customerRepository, ReservationRepository reservationRepository) {
//        return args -> {
//
//            //Create and save new customer
//            CustomerEntity customer1 = new CustomerEntity("Jan","Kowalski");
//            CustomerEntity customer2 = new CustomerEntity("Patryk","Sieńkowski");
//            customerRepository.save(customer1);
//            customerRepository.save(customer2);
//
//            //Create and ave new reservations
//            reservationRepository.save(new ReservationEntity("Warszawa", new BigDecimal("220.00"), customer1));
//            reservationRepository.save(new ReservationEntity("Gdańsk", new BigDecimal("180.00"), customer1));
//            reservationRepository.save(new ReservationEntity("Sopot", new BigDecimal("190.00"), customer2));
//        };
//    }

}

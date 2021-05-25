package com.lordeats.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
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

package com.lordeats.api.controllers;

import com.lordeats.api.dtos.*;
import com.lordeats.api.entities.CustomerEntity;
import com.lordeats.api.services.CustomerService;
import com.lordeats.api.services.LoginAndRegisterService;
import com.lordeats.api.services.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "https://web-eats-react.herokuapp.com/")
@RestController
@Slf4j
public class Controller {

    private final CustomerService customerService;
    private final ReservationService reservationService;
    private final LoginAndRegisterService loginAndRegisterService;

    @Autowired
    public Controller(CustomerService customerService, ReservationService reservationService, LoginAndRegisterService loginAndRegisterService) {
        this.customerService = customerService;
        this.reservationService = reservationService;
        this.loginAndRegisterService = loginAndRegisterService;
    }


    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome on WebEats Server!";
    }

    //Customer control

    @GetMapping("/api/getCustomers")
    public ResponseEntity<List<GetCustomer>> getAllCustomers() {
        return new ResponseEntity<>(customerService.getAllCustomers(), HttpStatus.OK);
    }

    @PostMapping("/api/addCustomer")
    public ResponseEntity<Map<String, String>> addNewCustomer(@RequestBody PostCustomer postCustomer) {
        log.info("User register payload: [Nickname]: " + postCustomer.getNickname() + " [Password]: " + postCustomer.getPassword() + ".");
        CustomerEntity customer = customerService.addNewCustomer(postCustomer);
        Map<String, String> map = new HashMap<>();
        if(customer != null) {
            map.put("userId", String.valueOf(customer.getId()));
            map.put("nickname", customer.getNickname());
            log.info("Account created Successfully.");
            return new ResponseEntity<>(map, HttpStatus.CREATED);
        }
        map.put("message", "This users already exists.");
        return new ResponseEntity<>(map, HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping("/api/logInCustomer")
    public ResponseEntity<Map<String, String>> logInCustomer(@RequestBody PostCustomer postCustomer) {
        log.info("User login payload: [Nickname]: " + postCustomer.getNickname() + " [Password]: " + postCustomer.getPassword() + ".");
        CustomerEntity customer = customerService.logInCustomer(postCustomer);
        Map<String, String> map = new HashMap<>();
        if(customer != null) {
            map.put("userId", String.valueOf(customer.getId()));
            map.put("nickname", customer.getNickname());
            log.info("Logged in Successfully.");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        map.put("message", "Wrong nickname or password!");
        return new ResponseEntity<>(map, HttpStatus.NOT_IMPLEMENTED);
    }

    //TODO zrobić w react
    @PostMapping("/api/logOutCustomer")
    public ResponseEntity<Map<String, String>> logOutCustomer(@RequestBody PostCustomer postCustomer) {
        log.info("User logout payload: [Nickname]: " + postCustomer.getNickname() + ".");
        boolean isLogOut = customerService.logOutCustomer(postCustomer);
        Map<String, String> map = new HashMap<>();
        if(isLogOut) {
            log.info("Logged out Successfully.");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        map.put("message", "Logged out Error!");
        return new ResponseEntity<>(map, HttpStatus.NOT_IMPLEMENTED);
    }

    @PutMapping("/api/updateCustomer")
    public ResponseEntity updateCustomer(@RequestBody UpdateCustomer updateCustomer) {
        boolean isUpdate = customerService.updateCustomer(updateCustomer);
        if(isUpdate)
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/api/deleteCustomer")
    public ResponseEntity<Integer> deleteCustomer(@RequestBody int id) {
        boolean isDelete = customerService.deleteCustomer(id);
        if(isDelete)
            return new ResponseEntity<>(id, HttpStatus.OK);
        return new ResponseEntity<>(id, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/api/deleteAllCustomers")
    public ResponseEntity deleteAllCustomers() {
        boolean isDelete = customerService.deleteAllCustomers();
        if(isDelete)
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //Reservation control

    @GetMapping("/api/getReservations")
    public ResponseEntity<List<GetReservation>> getAllReservations() {
        return new ResponseEntity<>(reservationService.getAllReservations(), HttpStatus.OK);
    }

    @GetMapping("/api/getReservation/{reservationId}")
    public ResponseEntity<GetReservation> getReservation(@PathVariable String reservationId) {
        GetReservation reservation = reservationService.getReservation(Integer.parseInt(reservationId));
        if (reservation != null){
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/api/getUserReservations/{customerId}")
    public ResponseEntity<List<GetReservation>> getUserReservations(@PathVariable String customerId) {
        log.info("User : " + customerId + " wants to view reservations.");
        return new ResponseEntity<>(reservationService.getCustomerReservations(customerId), HttpStatus.OK);
    }

    @PostMapping("/api/addReservation")
    public ResponseEntity<Map<String, String>> addNewReservation(@RequestBody PostReservation postReservation) {
        log.info("Add Reservation payload: " + postReservation.getCustomerNickname() + ".");
        boolean isCreated = reservationService.addNewReservation(postReservation);
        if(isCreated)
            return new ResponseEntity<>(HttpStatus.CREATED);
        Map<String, String> map = new HashMap<>();
        map.put("message", "The message could not be added!");
        return new ResponseEntity<>(map, HttpStatus.NOT_IMPLEMENTED);
    }

    @PutMapping("/api/updateReservation/{reservationId}")
    public ResponseEntity<Map<String, String>> updateReservation(@PathVariable String reservationId, @RequestBody UpdateReservation updateReservation) {
        log.info("Update Reservation payload: [Id]: " + reservationId + " [Data]: " + updateReservation.getName() + ".");
        boolean isUpdate = reservationService.updateReservation(Integer.parseInt(reservationId), updateReservation);
        Map<String, String> map = new HashMap<>();
        if(isUpdate)
            return new ResponseEntity<>(map, HttpStatus.OK);
        map.put("message", "Reservation cannot be updated!");
        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/api/deleteReservation/{reservationId}")
    public ResponseEntity<Map<String, String>> deleteReservation(@PathVariable String reservationId) {
        boolean isDelete = reservationService.deleteReservation(Integer.parseInt(reservationId));
        Map<String, String> map = new HashMap<>();
        if(isDelete)
            return new ResponseEntity<>(map, HttpStatus.OK);
        map.put("message", "Reservation cannot be deleted!");
        return new ResponseEntity<>(map, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/api/deleteAllReservations")
    public ResponseEntity deleteAllReservations() {
        boolean isDelete = reservationService.deleteAllReservations();
        if(isDelete)
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Login Data

    @GetMapping("/api/getLoginCustomers")
    public ResponseEntity<String> getLoginCustomers() {
        return new ResponseEntity<>(loginAndRegisterService.listLogInUsers(), HttpStatus.OK);
    }
}

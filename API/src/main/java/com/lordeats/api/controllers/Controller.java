package com.lordeats.api.controllers;

import com.lordeats.api.dtos.*;
import com.lordeats.api.services.CustomerService;
import com.lordeats.api.services.LoginAndRegisterService;
import com.lordeats.api.services.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
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
    public ResponseEntity addNewCustomer(@RequestBody PostCustomer postCustomer) {
        boolean isCreated = customerService.addNewCustomer(postCustomer);
        if(isCreated)
            return new ResponseEntity<>(HttpStatus.CREATED);
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
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

    @PostMapping("/api/addReservation")
    public ResponseEntity addNewReservation(@RequestBody PostReservation postReservation) {
        boolean isCreated = reservationService.addNewReservation(postReservation);
        if(isCreated)
            return new ResponseEntity<>(HttpStatus.CREATED);
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @PutMapping("/api/updateReservation")
    public ResponseEntity updateReservation(@RequestBody UpdateReservation updateReservation) {
        boolean isUpdate = reservationService.updateReservation(updateReservation);
        if(isUpdate)
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/api/deleteReservation")
    public ResponseEntity<Integer> deleteReservation(@RequestBody int id) {
        boolean isDelete = reservationService.deleteReservation(id);
        if(isDelete)
            return new ResponseEntity<>(id, HttpStatus.OK);
        return new ResponseEntity<>(id, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/api/deleteAllReservations")
    public ResponseEntity deleteAllReservations() {
        boolean isDelete = reservationService.deleteAllReservations();
        if(isDelete)
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Dane logowania

    @GetMapping("/api/getLoginCustomers")
    public ResponseEntity<String> getLoginCustomers() {
        return new ResponseEntity<>(loginAndRegisterService.listLogInUsers(), HttpStatus.OK);
    }
}

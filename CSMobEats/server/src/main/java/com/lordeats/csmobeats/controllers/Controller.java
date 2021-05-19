package com.lordeats.csmobeats.controllers;

import com.lordeats.csmobeats.dtos.*;
import com.lordeats.csmobeats.services.CustomerService;
import com.lordeats.csmobeats.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Controller {

    private final CustomerService customerService;
    private final ReservationService reservationService;

    @Autowired
    public Controller(CustomerService customerService, ReservationService reservationService) {
        this.customerService = customerService;
        this.reservationService = reservationService;
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
}

package com.lordeats.api.services;

import com.lordeats.api.dtos.GetReservation;
import com.lordeats.api.entities.CustomerEntity;
import com.lordeats.api.entities.ReservationEntity;
import com.lordeats.api.repositories.CustomerRepository;
import com.lordeats.api.repositories.ReservationRepository;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
public class LoginAndRegisterServiceImpl implements LoginAndRegisterService {

    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;
    private final int strength = 10;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(strength, new SecureRandom());

    @Autowired
    public LoginAndRegisterServiceImpl(CustomerRepository customerRepository, ReservationRepository reservationRepository) {
        this.customerRepository = customerRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public synchronized boolean registerUser(JSONObject registerPayload) {
        try {
            if(customerRepository.existsByNickname(registerPayload.getString("nickname"))) {
                return false;
            }
            CustomerEntity customer = new CustomerEntity();
            customer.setNickname(registerPayload.getString("nickname"));
            String encodedPassword = bCryptPasswordEncoder.encode(registerPayload.getString("password"));
            customer.setPassword(encodedPassword);

            customerRepository.save(customer);
            return customerRepository.existsById(customer.getId());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public synchronized boolean logInUser(JSONObject loginPayload, String sessionId) {
        try {
            String nickname = loginPayload.getString("nickname");
            String password = loginPayload.getString("password");
            if(customerRepository.existsByNickname(nickname)) {
                CustomerEntity customer = customerRepository.findByNickname(nickname);
                if(bCryptPasswordEncoder.matches(password,customer.getPassword())){
                    loginUsersHashMap.put(sessionId, nickname);
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public synchronized boolean changeData(JSONObject changeDataPayload) {
        String newNickname;
        String newPassword;
        try {
            CustomerEntity customer = customerRepository.findByNickname(changeDataPayload.getString("nickname"));
            if(customer != null) {
                if(changeDataPayload.getString("type").equals("nickname")) {
                    newNickname = changeDataPayload.getString("newNickname");
                    if(customerRepository.existsByNickname(newNickname)){
                        return false;
                    }
                    customer.setNickname(newNickname);
                } else if(changeDataPayload.getString("type").equals("password")) {
                    newPassword = changeDataPayload.getString("newPassword");
                    String encodedPassword = bCryptPasswordEncoder.encode(newPassword);
                    customer.setPassword(encodedPassword);
                } else {
                    return false;
                }
                customerRepository.save(customer);
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public synchronized boolean removeUser(JSONObject userPayload) {
        String nickname;
        try {
            nickname = userPayload.getString("nickname");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        if(customerRepository.existsByNickname(nickname)){
            CustomerEntity customer = customerRepository.findByNickname(nickname);
            if(customer.hasReservations()) {
                for(Integer reservationId: customer.getReservationsId()){
                    reservationRepository.deleteById(reservationId);
                }
            }
            customerRepository.deleteById(customer.getId());
        }
        return !customerRepository.existsByNickname(nickname);
    }

    @Override
    public void logOutUser(String sessionId) {
        loginUsersHashMap.remove(sessionId);
    }

    @Override
    public String listLogInUsers() {
        if(!loginUsersHashMap.isEmpty()){
            return loginUsersHashMap.toString();
        }
        return "No logged in users";
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

    @Override
    public List<GetReservation> userListReservations(String nickname) {
        List<GetReservation> listReservations = new ArrayList<>();
        if(customerRepository.existsByNickname(nickname)){
            CustomerEntity customer =  customerRepository.findByNickname(nickname);
            for(int reservation_id: customer.getReservationsId()) {
                GetReservation reservation = getReservation(reservation_id);
                if(reservation != null)
                    listReservations.add(reservation);
            }
        }
        return listReservations;
    }
}
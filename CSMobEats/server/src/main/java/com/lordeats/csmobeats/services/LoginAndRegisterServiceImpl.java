package com.lordeats.csmobeats.services;

import com.lordeats.csmobeats.dtos.GetCustomer;
import com.lordeats.csmobeats.entities.CustomerEntity;
import com.lordeats.csmobeats.repositories.CustomerRepository;
import com.lordeats.csmobeats.repositories.ReservationRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class LoginAndRegisterServiceImpl implements LoginAndRegisterService {

    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;

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
            customer.setPassword(registerPayload.getString("password"));

            customerRepository.save(customer);
//        registeredUsersHashMap.add(registerPayload);
            return customerRepository.existsById(customer.getId());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public synchronized boolean logInUser(JSONObject loginPayload, String sessionId) {
        try {
            if(customerRepository.existsByNickname(loginPayload.getString("nickname"))) {
                loginUsersHashMap.put(sessionId, loginPayload);
                return true;
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
                    customer.setPassword(newPassword);
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
            for(Integer reservationId: customer.getReservationsId()){
                reservationRepository.deleteById(reservationId);
            }
            customerRepository.deleteById(customer.getId());
            return true;
        }
        return false;
    }

    @Override
    public void logOutUser(String sessionId) {
        loginUsersHashMap.remove(sessionId);
    }

    @Override
    public JSONObject getUser(String sessionId) {
        if(loginUsersHashMap.containsKey(sessionId)){
            return loginUsersHashMap.get(sessionId);
        }
        return null;
    }

    @Override
    public String listRegisteredUsers() {
        List<GetCustomer> listCustomers = StreamSupport.stream(customerRepository.findAll().spliterator(), false).map(customerEntity -> new GetCustomer(customerEntity.getId(),customerEntity.getNickname(),customerEntity.getPassword(),customerEntity.getReservationsId())).collect(Collectors.toList());
        return listCustomers.toString();
    }

    @Override
    public String listLogInUsers() {

        var sb = new StringBuilder();

        for(var value: loginUsersHashMap.values()){
            sb.append(value).append("\n");
        }

        return sb.toString();
    }
}

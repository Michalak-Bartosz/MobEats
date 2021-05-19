package com.lordeats.csmobeats.services;

import com.lordeats.csmobeats.dtos.GetCustomer;
import com.lordeats.csmobeats.dtos.PostCustomer;
import com.lordeats.csmobeats.dtos.UpdateCustomer;
import com.lordeats.csmobeats.entities.CustomerEntity;
import com.lordeats.csmobeats.repositories.CustomerRepository;
import com.lordeats.csmobeats.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, ReservationRepository reservationRepository) {
        this.customerRepository = customerRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<GetCustomer> getAllCustomers() {
        return StreamSupport.stream(customerRepository.findAll().spliterator(), false).map(customerEntity -> new GetCustomer(customerEntity.getId(),customerEntity.getNickname(),customerEntity.getPassword(),customerEntity.getReservationsId())).collect(Collectors.toList());
    }

    @Override
    public boolean addNewCustomer(PostCustomer postCustomer) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setNickname(postCustomer.getNickname());
        customerEntity.setPassword(postCustomer.getPassword());

        customerRepository.save(customerEntity);
        return customerRepository.existsById(customerEntity.getId());
    }

    @Override
    public boolean updateCustomer(UpdateCustomer updateCustomer) {
        if(customerRepository.existsById(updateCustomer.getId())) {
            CustomerEntity customer = customerRepository.findById(updateCustomer.getId());
            customer.setNickname(updateCustomer.getNickname());
            customer.setPassword(updateCustomer.getPassword());
            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteCustomer(int id) {
        if(customerRepository.existsById(id)){
            CustomerEntity customer = customerRepository.findById(id);
            for(Integer reservationId: customer.getReservationsId()){
                reservationRepository.deleteById(reservationId);
            }
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

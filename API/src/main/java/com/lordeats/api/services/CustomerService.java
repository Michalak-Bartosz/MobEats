package com.lordeats.api.services;

import com.lordeats.api.dtos.GetCustomer;
import com.lordeats.api.dtos.PostCustomer;
import com.lordeats.api.dtos.UpdateCustomer;
import com.lordeats.api.entities.CustomerEntity;

import java.util.List;

public interface CustomerService {
    List<GetCustomer> getAllCustomers();

    CustomerEntity addNewCustomer(PostCustomer request);

    CustomerEntity logInCustomer(PostCustomer request);

    boolean logOutCustomer(PostCustomer request);

    boolean updateCustomer(UpdateCustomer updateCustomer);

    boolean deleteCustomer(int id);

    boolean deleteAllCustomers();
}

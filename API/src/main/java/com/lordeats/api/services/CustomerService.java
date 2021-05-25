package com.lordeats.api.services;

import com.lordeats.api.dtos.GetCustomer;
import com.lordeats.api.dtos.PostCustomer;
import com.lordeats.api.dtos.UpdateCustomer;

import java.util.List;

public interface CustomerService {
    List<GetCustomer> getAllCustomers();

    boolean addNewCustomer(PostCustomer request);

    boolean updateCustomer(UpdateCustomer updateCustomer);

    boolean deleteCustomer(int id);

    boolean deleteAllCustomers();
}

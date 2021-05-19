package com.lordeats.csmobeats.services;

import com.lordeats.csmobeats.dtos.GetCustomer;
import com.lordeats.csmobeats.dtos.PostCustomer;
import com.lordeats.csmobeats.dtos.UpdateCustomer;

import java.util.List;

public interface CustomerService {
    List<GetCustomer> getAllCustomers();

    boolean addNewCustomer(PostCustomer request);

    boolean updateCustomer(UpdateCustomer updateCustomer);

    boolean deleteCustomer(int id);
}

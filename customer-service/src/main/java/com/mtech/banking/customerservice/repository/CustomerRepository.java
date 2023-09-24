package com.mtech.banking.customerservice.repository;

import com.mtech.banking.customerservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

  boolean existsByEmailOrPhone(String email, String phone);

  boolean existsByPhone(String phone);
}

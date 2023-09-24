package com.mtech.banking.customerservice.controller;

import com.mtech.banking.customerservice.dto.CustomerCreateDTO;
import com.mtech.banking.customerservice.dto.CustomerDTO;
import com.mtech.banking.customerservice.dto.CustomerUpdateDTO;
import com.mtech.banking.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;

  @GetMapping("/{id}")
  ResponseEntity<CustomerDTO> findCustomerById(@PathVariable Long id) {
    return ResponseEntity.ok(customerService.findCustomerById(id));
  }

  @PostMapping
  ResponseEntity<CustomerDTO> createCustomer(@RequestBody @Valid CustomerCreateDTO payload) {
    CustomerDTO persistedCustomer = customerService.createCustomer(payload);
    return ResponseEntity.created(
            URI.create(String.format("/customers/%d", persistedCustomer.id())))
        .body(persistedCustomer);
  }

  @PutMapping("/{id}")
  ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody @Valid CustomerUpdateDTO payload) {
    return ResponseEntity.ok(customerService.updateCustomer(id, payload));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteCustomer(@PathVariable Long id) {
    customerService.deleteCustomer(id);
  }

}

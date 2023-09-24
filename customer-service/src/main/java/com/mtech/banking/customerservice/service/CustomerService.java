package com.mtech.banking.customerservice.service;

import com.mtech.banking.customerservice.dto.CustomerCreateDTO;
import com.mtech.banking.customerservice.dto.CustomerDTO;
import com.mtech.banking.customerservice.dto.CustomerUpdateDTO;
import com.mtech.banking.customerservice.exception.BusinessException;
import com.mtech.banking.customerservice.exception.ErrorSource;
import com.mtech.banking.customerservice.exception.ErrorType;
import com.mtech.banking.customerservice.exception.NotFoundException;
import com.mtech.banking.customerservice.mapper.CustomerMapper;
import com.mtech.banking.customerservice.model.Customer;
import com.mtech.banking.customerservice.repository.CustomerRepository;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  public CustomerDTO findCustomerById(Long id) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(
            () -> new NotFoundException(ErrorSource.CUSTOMER,
                String.format("CustomerId %d not found", id),
                ErrorType.RESOURCE_NOT_FOUND));

    return customerMapper.entityToDTO(customer);
  }

  @Transactional
  public CustomerDTO createCustomer(CustomerCreateDTO customerDTO) {
    Customer customer = customerMapper.createDTOToEntity(customerDTO);

    validateCustomerForCreation(customer);
    customer.setCreatedAt(ZonedDateTime.now());
    Customer savedCustomer = null;
    savedCustomer = customerRepository.save(customer);

    return customerMapper.entityToDTO(savedCustomer);
  }


  @Transactional
  public CustomerDTO updateCustomer(Long id, CustomerUpdateDTO customerDTO) {
    Customer existingCustomer = customerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorSource.CUSTOMER,
            String.format("CustomerId %d not found", id),
            ErrorType.RESOURCE_NOT_FOUND));

    validatePhoneUpdate(existingCustomer, customerDTO);

    customerMapper.updateCustomerFromDto(customerDTO, existingCustomer);

    existingCustomer.setUpdatedAt(ZonedDateTime.now());

    Customer updatedCustomer = customerRepository.save(existingCustomer);

    return customerMapper.entityToDTO(updatedCustomer);
  }

  @Transactional
  public void deleteCustomer(Long id) {
    customerRepository.deleteById(id);
  }

  private void validateCustomerForCreation(Customer customer) {
    if (customerRepository.existsByEmailOrPhone(customer.getEmail(), customer.getPhone())) {
      throw new BusinessException(ErrorSource.CUSTOMER,
          "A customer with the same email or phone already exists",
          ErrorType.RESOURCE_ALREADY_EXISTS);
    }
  }

  private void validatePhoneUpdate(Customer customer, CustomerUpdateDTO customerDTO) {
    if (!customer.getPhone().equals(customerDTO.phone()) && customerRepository.existsByPhone(
        customerDTO.phone())) {
      throw new BusinessException(ErrorSource.CUSTOMER,
          "A customer with the same phone already exists",
          ErrorType.RESOURCE_ALREADY_EXISTS);
    }
  }
}

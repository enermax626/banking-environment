package com.mtech.banking.customerservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mtech.banking.customerservice.dto.CustomerCreateDTO;
import com.mtech.banking.customerservice.dto.CustomerDTO;
import com.mtech.banking.customerservice.dto.CustomerUpdateDTO;
import com.mtech.banking.customerservice.exception.BusinessException;
import com.mtech.banking.customerservice.exception.ErrorSource;
import com.mtech.banking.customerservice.exception.ErrorType;
import com.mtech.banking.customerservice.exception.NotFoundException;
import com.mtech.banking.customerservice.model.Customer;
import com.mtech.banking.customerservice.model.Gender;
import com.mtech.banking.customerservice.repository.CustomerRepository;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@RequiredArgsConstructor
class CustomerServiceTest {

  @Autowired
  private CustomerService customerService;

  @MockBean
  private CustomerRepository customerRepository;

  @Test
  void shouldFindACustomerWhenValidCustomerId() {
    //given
    Customer expectedCustomer = Customer.builder()
        .id(1L)
        .firstName("John")
        .lastName("Doe")
        .build();

    when(customerRepository.findById(1L)).thenReturn(Optional.of(expectedCustomer));

    // when
    CustomerDTO customerById = customerService.findCustomerById(1L);

    // then
    assertNotNull(customerById);
    assertEquals(expectedCustomer.getId(), customerById.id());
    assertEquals(expectedCustomer.getFirstName(), customerById.firstName());
    assertEquals(expectedCustomer.getLastName(), customerById.lastName());
  }

  @Test
  void shouldThrowNotFoundWhenNoCustomerId() {
    //given
    Long id = 1L;

    when(customerRepository.findById(id)).thenThrow(new NotFoundException(ErrorSource.CUSTOMER,
        String.format("CustomerId %d not found", id),
        ErrorType.RESOURCE_NOT_FOUND));

    // then
    assertThrows(NotFoundException.class, () -> customerService.findCustomerById(id));
  }

  @Test
  void shouldCreateCustomer() {
    // given
    CustomerCreateDTO customerCreateDTO = new CustomerCreateDTO(
        "John",
        "Doe",
        ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
        Gender.MALE,
        "test@test.com",
        "1234567890"
    );
    Customer expectedCustomer = Customer.builder()
        .firstName(customerCreateDTO.firstName())
        .lastName(customerCreateDTO.lastName())
        .dateOfBirth(customerCreateDTO.dateOfBirth())
        .gender(customerCreateDTO.gender())
        .email(customerCreateDTO.email())
        .phone(customerCreateDTO.phone())
        .build();

    ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
    when(customerRepository.save(customerArgumentCaptor.capture())).thenReturn(expectedCustomer);

    // when
    CustomerDTO customerDTO = customerService.createCustomer(customerCreateDTO);

    // then

    assertNotNull(customerDTO);
    assertEquals(expectedCustomer.getFirstName(), customerDTO.firstName());
    assertEquals(expectedCustomer.getLastName(), customerDTO.lastName());
    assertEquals(expectedCustomer.getDateOfBirth(), customerDTO.dateOfBirth());

    Customer capturedCustomer = customerArgumentCaptor.getValue();
    assertEquals(expectedCustomer.getFirstName(), capturedCustomer.getFirstName());
    assertEquals(expectedCustomer.getLastName(), capturedCustomer.getLastName());
    assertEquals(expectedCustomer.getDateOfBirth(), capturedCustomer.getDateOfBirth());
    assertEquals(expectedCustomer.getGender(), capturedCustomer.getGender());
    assertEquals(expectedCustomer.getEmail(), capturedCustomer.getEmail());
    assertEquals(expectedCustomer.getPhone(), capturedCustomer.getPhone());
  }

  @Test
  void shouldNotCreateCustomerIfEmailOrPhoneExists() {
    // Given
    CustomerCreateDTO customerCreateDTO = new CustomerCreateDTO(
        "John",
        "Doe",
        ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
        Gender.MALE,
        "test@test.com",
        "1234567890"
    );

    when(customerRepository.existsByEmailOrPhone(customerCreateDTO.email(),
        customerCreateDTO.phone()))
        .thenReturn(true);

    // Then
    assertThrows(BusinessException.class, () -> customerService.createCustomer(customerCreateDTO));
  }

  @Test
  void shouldUpdateCustomer() {
    // Given
    Long id = 1L;
    CustomerUpdateDTO customerUpdateDTO = new CustomerUpdateDTO(
        "John",
        "Doe",
        ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
        Gender.MALE,
        "1234567890"
    );
    Customer existingCustomer = Customer.builder()
        .id(id)
        .firstName("OldJohn")
        .lastName("OldDoe")
        .phone("1234567890")
        .email("test@test.com")
        .dateOfBirth(ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
        .createdAt(ZonedDateTime.of(1980, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
        .gender(Gender.MALE)
        .build();

    when(customerRepository.existsByPhone(
        customerUpdateDTO.phone())).thenReturn(false);
    when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
    when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);

    // When
    CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerUpdateDTO);

    // Then
    assertNotNull(updatedCustomer);
    assertEquals("John", updatedCustomer.firstName());
    assertEquals("Doe", updatedCustomer.lastName());
  }

  @Test
  void shouldThrowExceptionWhenUpdateNonExistentCustomer() {
    // Given
    Long id = 1L;
    CustomerUpdateDTO customerUpdateDTO = new CustomerUpdateDTO(
        "John",
        "Doe",
        ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
        Gender.MALE,
        "1234567890"
    );

    when(customerRepository.findById(id)).thenReturn(Optional.empty());

    // Then
    assertThrows(NotFoundException.class,
        () -> customerService.updateCustomer(id, customerUpdateDTO));
  }

  @Test
  void shouldDeleteCustomer() {
    // Given
    Long id = 1L;
    doNothing().when(customerRepository).deleteById(id);

    // When
    customerService.deleteCustomer(id);

    // Then
    verify(customerRepository, times(1)).deleteById(id);
  }
}
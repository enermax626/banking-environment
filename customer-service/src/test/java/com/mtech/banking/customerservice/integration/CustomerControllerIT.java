package com.mtech.banking.customerservice.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtech.banking.customerservice.MySQLCleanUpExtension;
import com.mtech.banking.customerservice.dto.CustomerCreateDTO;
import com.mtech.banking.customerservice.dto.CustomerUpdateDTO;
import com.mtech.banking.customerservice.model.Customer;
import com.mtech.banking.customerservice.model.Gender;
import com.mtech.banking.customerservice.repository.CustomerRepository;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("integration")
@ExtendWith(MySQLCleanUpExtension.class)
public class CustomerControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CustomerRepository customerRepository;


  @Test
  public void shouldCreateCustomer() throws Exception {
    // given
    CustomerCreateDTO payload = new CustomerCreateDTO(
        "John", "Doe", ZonedDateTime.now(), Gender.MALE, "testCreate@test.com", "1236667890"
    );
    // when
    ResultActions perform = mockMvc.perform(post("/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payload))
    );
    // then
    MvcResult mvcResult = perform.andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Doe"))
        .andReturn();

    Customer persistedCustomer = customerRepository.findAll().stream().findFirst().orElseThrow();

    assertAll(
        () -> assertEquals("John", persistedCustomer.getFirstName()),
        () -> assertEquals("Doe", persistedCustomer.getLastName()),
        () -> assertEquals("testCreate@test.com", persistedCustomer.getEmail()),
        () -> assertEquals("1236667890", persistedCustomer.getPhone()));
  }

  @Test
  public void shouldUpdateCustomer() throws Exception {
    //given
    CustomerUpdateDTO payload = new CustomerUpdateDTO(
        "Murilo", "Toloni", ZonedDateTime.now(), Gender.MALE, "9876543210"
    );

    ZonedDateTime createdAt = ZonedDateTime.now();
    Customer customerToPersist = Customer.builder()
        .firstName("John")
        .lastName("Doe")
        .gender(Gender.MALE)
        .email("test@test.com")
        .phone("1234567890")
        .createdAt(createdAt)
        .build();

    // when
    Customer persistedCustomer = customerRepository.save(customerToPersist);
    assertNull(persistedCustomer.getUpdatedAt());

    ResultActions perform = mockMvc.perform(put("/customers/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payload))
    );

    Customer updatedCustomer = customerRepository.findById(persistedCustomer.getId()).orElseThrow();

    // then
    perform.andExpect(status().isOk())  // Assuming the update operation returns HTTP 200 OK
        .andExpect(jsonPath("$.firstName").value("Murilo"))
        .andExpect(jsonPath("$.lastName").value("Toloni"))
        .andExpect(jsonPath("$.phone").value("9876543210"))
        .andExpect(jsonPath("$.gender").value("MALE"));

    assertEquals(payload.firstName(), updatedCustomer.getFirstName());
    assertEquals(payload.lastName(), updatedCustomer.getLastName());
    assertEquals(payload.phone(), updatedCustomer.getPhone());
  }
}
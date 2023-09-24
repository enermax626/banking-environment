package com.mtech.banking.customerservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtech.banking.customerservice.dto.CustomerCreateDTO;
import com.mtech.banking.customerservice.dto.CustomerDTO;
import com.mtech.banking.customerservice.dto.CustomerUpdateDTO;
import com.mtech.banking.customerservice.model.Customer;
import com.mtech.banking.customerservice.model.Gender;
import com.mtech.banking.customerservice.service.CustomerService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = CustomerController.class)
@AutoConfigureMockMvc
class CustomerControllerTest {

  @MockBean
  private CustomerService customerService;
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldFindCustomerByIdWhenExistentId() throws Exception {
    // given
    Long id = 1L;

    CustomerDTO expectedCustomer = new CustomerDTO(id, "John", "Doe", ZonedDateTime.now(),
        Gender.MALE,
        "test@test.com", "1234567890");

    when(customerService.findCustomerById(id)).thenReturn(expectedCustomer);

    // then
    mockMvc.perform(get("/customers/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Doe"));
  }

  @Test
  void shouldCreateCustomer() throws Exception {
    // given
    CustomerCreateDTO payload = new CustomerCreateDTO(
        "John", "Doe", ZonedDateTime.now(), Gender.MALE, "test@test.com", "1234567890"
    );

    CustomerDTO expectedCustomer = new CustomerDTO(
        1L, "John", "Doe", ZonedDateTime.now(), Gender.MALE, "test@test.com", "1234567890"
    );

    ArgumentCaptor<CustomerCreateDTO> customerCreateDTOArgumentCaptor = ArgumentCaptor
        .forClass(CustomerCreateDTO.class);
    when(customerService.createCustomer(customerCreateDTOArgumentCaptor.capture())).thenReturn(expectedCustomer);


    // when
    ResultActions perform = mockMvc.perform(post("/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payload)));


    // then
    CustomerCreateDTO capturedCustomer = customerCreateDTOArgumentCaptor.getValue();
    perform.andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName").value(capturedCustomer.firstName()))
        .andExpect(jsonPath("$.lastName").value(capturedCustomer.lastName()));
  }

  @Test
  void shouldUpdateCustomer() throws Exception {
    // given
    Long id = 1L;
    CustomerUpdateDTO payload = new CustomerUpdateDTO(
        "Murilo", "Toloni", ZonedDateTime.now(), Gender.MALE,  "1234567890"
    );

    CustomerDTO expectedCustomer = new CustomerDTO(
        id, payload.firstName(), payload.lastName(), payload.dateOfBirth(), payload.gender(), "test@test.com", payload.phone()
    );

    ArgumentCaptor<CustomerUpdateDTO> customerUpdateDTOArgumentCaptor = ArgumentCaptor
        .forClass(CustomerUpdateDTO.class);
    when(customerService.updateCustomer(any(), customerUpdateDTOArgumentCaptor.capture())).thenReturn(expectedCustomer);


    // when
    ResultActions perform = mockMvc.perform(put("/customers/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payload)));


    // then
    CustomerUpdateDTO capturedCustomer = customerUpdateDTOArgumentCaptor.getValue();
    perform.andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value(capturedCustomer.firstName()))
        .andExpect(jsonPath("$.lastName").value(capturedCustomer.lastName()));
  }

  @Test
  void shouldDeleteCostumer() throws Exception {

    ResultActions perform = mockMvc.perform(delete("/customers/1"))
        .andExpect(status().isNoContent());
  }
}
package com.mtech.banking.customerservice.dto;

import com.mtech.banking.customerservice.model.Gender;
import java.time.ZonedDateTime;

public record CustomerDTO(
    Long id,
    String firstName,
    String lastName,
    ZonedDateTime dateOfBirth,
    Gender gender,
    String email,
    String phone) {

}

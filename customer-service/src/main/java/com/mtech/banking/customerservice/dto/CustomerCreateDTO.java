package com.mtech.banking.customerservice.dto;

import com.mtech.banking.customerservice.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public record CustomerCreateDTO(
    @NotBlank(message = "First name is required")
    String firstName,
    @NotBlank(message = "Last name is required")
    String lastName,
    @NotNull(message = "Date of birth is required")
    ZonedDateTime dateOfBirth,
    @NotNull(message = "Gender is required")
    Gender gender,
    @Email(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", message = "Email is invalid")
    String email,
    @NotBlank(message = "Phone is required")
    String phone) {

}

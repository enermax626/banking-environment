package com.mtech.banking.customerservice.dto;

import com.mtech.banking.customerservice.model.Gender;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public record CustomerUpdateDTO(@NotEmpty(message = "First name is required") String firstName,
                                @NotEmpty(message = "Last name is required") String lastName,
                                @NotNull(message = "Date of birth is required") ZonedDateTime dateOfBirth,
                                @NotNull(message = "Gender is required") Gender gender,
                                @NotEmpty(message = "Phone is required") String phone) {

}

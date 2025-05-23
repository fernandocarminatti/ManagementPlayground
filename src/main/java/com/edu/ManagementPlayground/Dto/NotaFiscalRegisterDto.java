package com.edu.ManagementPlayground.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record NotaFiscalRegisterDto(
        @NotBlank(message = "Must contain identifier number.")
        String numberIdentifier,

        @NotNull(message = "Issue date must not be null")
        @PastOrPresent(message = "Date must be on Past or Present.")
        LocalDate issueDate,

        @Positive(message = "Must be bigger than 0.")
        double totalValue,

        @NotNull(message = "A File reference must be provided.")
        String fileReference,

        @NotNull(message = "Supplier identification must be assigned.")
        Long supplierId
) {}
package com.edu.ManagementPlayground.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NotaFiscalRegisterDto(
        @NotBlank(message = "Must contain identifier number.")
        String numberIdentifier,

        @NotNull(message = "Issue date must not be null")
        @PastOrPresent(message = "Date must be on Past or Present.")
        LocalDate issueDate,

        @Positive(message = "Must be bigger than 0.")
        BigDecimal totalValue,

        @NotNull(message = "A File reference must be provided.")
        String fileReference,

        @NotNull(message = "Supplier identification must be assigned.")
        Long supplierId,

        @NotNull(message = "File must be provided. Supported formats are .pdf and .xml")
        MultipartFile objectFile
) {}
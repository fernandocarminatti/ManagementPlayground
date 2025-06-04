package com.edu.ManagementPlayground.Dto;

import com.edu.ManagementPlayground.Dto.CustomValidation.FileSize;
import com.edu.ManagementPlayground.Dto.CustomValidation.FileType;
import jakarta.validation.constraints.*;
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

        @NotNull(message = "Supplier identification must be assigned.")
        Long supplierId,

        @NotEmpty(message = "File must be provided.")
        @FileSize(max = 10 * 1024 * 1024, message = "File cannot exceed 10MB")
        @FileType(allowed = {"application/pdf", "application/xml"}, message = "Allowed formats are XML and PDF.")
        MultipartFile objectFile
) {}
package com.edu.ManagementPlayground.Dto;

import com.edu.ManagementPlayground.Dto.CustomValidation.FileSize;
import com.edu.ManagementPlayground.Dto.CustomValidation.FileType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BoletoRegisterDto(
        @NotBlank(message = "Must contain typeable line.")
        String typeableLine,

        @NotNull(message = "A date must be set.")
        @FutureOrPresent(message = "Date must be future or present.")
        LocalDate dueDate,

        @Positive(message = "Value must be positive.")
        BigDecimal value,

        int paymentStatus,

        @NotNull(message = "Nota Fiscal identification must be assigned.")
        long notaFiscalId,

        @NotNull(message = "A File must be present.")
        @FileSize(max = 10 * 1024 * 1024, message = "File cannot exceed 10MB")
        @FileType(allowed = {"application/pdf", "application/xml"}, message = "Allowed formats are XML and PDF.")
        MultipartFile boletoFile

) {}
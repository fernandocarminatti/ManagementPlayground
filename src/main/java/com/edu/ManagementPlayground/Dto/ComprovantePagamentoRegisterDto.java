package com.edu.ManagementPlayground.Dto;

import com.edu.ManagementPlayground.Dto.CustomValidation.FileSize;
import com.edu.ManagementPlayground.Dto.CustomValidation.FileType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ComprovantePagamentoRegisterDto(
        @NotNull(message = "A date must be set.")
        @PastOrPresent(message = "Payment Date must be future or present.")
        LocalDate paymentDate,

        @Positive(message = "Value must be positive.")
        BigDecimal value,

        @NotNull(message = "Boleto id must be assigned.")
        long boletoId,

        @NotNull(message = "A File must be present.")
        @FileSize(max = 10 * 1024 * 1024, message = "File cannot exceed 10MB")
        @FileType(allowed = {"application/pdf", "application/xml"}, message = "Allowed formats are XML and PDF.")
        MultipartFile comprovanteFile
){
}
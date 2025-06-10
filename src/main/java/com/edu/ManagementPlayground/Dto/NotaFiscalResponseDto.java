package com.edu.ManagementPlayground.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NotaFiscalResponseDto(long id, String numberIdentifier, LocalDate issueDate, BigDecimal totalValue, String fileReference, long supplierIdentifier) {
}
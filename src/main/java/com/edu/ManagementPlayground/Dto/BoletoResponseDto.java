package com.edu.ManagementPlayground.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BoletoResponseDto(long id, String typeableLine, LocalDate dueDate, BigDecimal value, int paymentStatus, String fileReference) {
}
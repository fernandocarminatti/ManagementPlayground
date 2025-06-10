package com.edu.ManagementPlayground.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ComprovantePagamentoResponseDto(long id, LocalDate paymentDate, BigDecimal value, Long boletoId, String fileReference){
}
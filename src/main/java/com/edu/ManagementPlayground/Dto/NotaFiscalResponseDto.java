package com.edu.ManagementPlayground.Dto;

import com.edu.ManagementPlayground.Entity.NotaFiscal;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NotaFiscalResponseDto(String numberIdentifier, LocalDate issueDate, BigDecimal totalValue, String fileReference, long supplierIdentifier) {

    public static NotaFiscalResponseDto fromEntity(NotaFiscal notaFiscal){
        return new NotaFiscalResponseDto(
                notaFiscal.getNumberIdentifier(),
                notaFiscal.getIssueDate(),
                notaFiscal.getTotalValue(),
                notaFiscal.getFileReference(),
                notaFiscal.getSupplier().getId() // Keep track of DB hits on this PROXY reference.
        );
    }
}
package com.edu.ManagementPlayground.Repository;

import com.edu.ManagementPlayground.Dto.ComprovantePagamentoResponseDto;
import com.edu.ManagementPlayground.Entity.ComprovantePagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ComprovantePagamentoRepository extends JpaRepository<ComprovantePagamento, Long> {
    Optional<ComprovantePagamento> findByBoletoId(Long boletoId);
    @Query("SELECT new com.edu.ManagementPlayground.Dto.ComprovantePagamentoResponseDto(cp.id, cp.paymentDate, cp.value, cp.boleto.id, cp.fileReference) FROM ComprovantePagamento cp")
    Set<ComprovantePagamentoResponseDto> findAllProjection();
}
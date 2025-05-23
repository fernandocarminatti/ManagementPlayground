package com.edu.ManagementPlayground.Repository;

import com.edu.ManagementPlayground.Dto.NotaFiscalResponseDto;
import com.edu.ManagementPlayground.Entity.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {
    boolean existsByNumberIdentifier(String numberIdentifier);
    Optional<NotaFiscal> findByNumberIdentifier(String numberIdentifier);
    @Query("SELECT new com.edu.ManagementPlayground.Dto.NotaFiscalResponseDto(nf.numberIdentifier, nf.issueDate, nf.totalValue, nf.fileReference, nf.supplier.id) FROM NotaFiscal nf")
    List<NotaFiscalResponseDto> findAllWithSupplierId();

}
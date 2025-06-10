package com.edu.ManagementPlayground.Repository;

import com.edu.ManagementPlayground.Dto.BoletoResponseDto;
import com.edu.ManagementPlayground.Entity.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    boolean existsByTypeableLine(String typeableLine);
    Optional<Boleto> findByTypeableLine(String typeableLine);
    @Query("SELECT new com.edu.ManagementPlayground.Dto.BoletoResponseDto(bl.id, bl.typeableLine, bl.dueDate, bl.value, bl.paymentStatus, bl.fileReference) FROM Boleto bl")
    Set<BoletoResponseDto> findAllProjection();
}
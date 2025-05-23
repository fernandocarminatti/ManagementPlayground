package com.edu.ManagementPlayground.Repository;

import com.edu.ManagementPlayground.Entity.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {
    boolean existsByNumberIdentifier(String numberIdentifier);
    Optional<NotaFiscal> findByNumberIdentifier(String numberIdentifier);
}
package com.edu.ManagementPlayground.Repository;

import com.edu.ManagementPlayground.Entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierService extends JpaRepository<Supplier, Long> {

    boolean existsByCnpj(String cnpj);
    Optional<Supplier> findByCnpj(String cnpj);
}
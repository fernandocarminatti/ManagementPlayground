package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.SupplierRegisterDto;
import com.edu.ManagementPlayground.Entity.Supplier;
import com.edu.ManagementPlayground.Exception.SupplierAlreadyExistsException;
import com.edu.ManagementPlayground.Exception.SupplierNotFoundException;
import com.edu.ManagementPlayground.Repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private com.edu.ManagementPlayground.Service.SupplierService supplierService;

    private SupplierRegisterDto supplierDto;

    @BeforeEach
    void setUp() {
        supplierDto = new SupplierRegisterDto(
                "ACME Corp",
                "12.345.678/0001-99",
                "contact@acme.com",
                "555-0101"
        );
    }

    @Nested
    @DisplayName("Tests for getAllSuppliers")
    class GetAllSuppliersTests {

        @Test
        @DisplayName("Should return a list of suppliers when suppliers exist")
        void getAllSuppliers_ShouldReturnListOfSuppliers() {
            // Arrange
            List<Supplier> expectedSuppliers = List.of(
                    new Supplier("Supplier A", "111", "a@a.com", "111"),
                    new Supplier("Supplier B", "222", "b@b.com", "222")
            );
            when(supplierRepository.findAll()).thenReturn(expectedSuppliers);

            // Act
            List<Supplier> actualSuppliers = supplierService.getAllSuppliers();

            // Assert
            assertNotNull(actualSuppliers);
            assertEquals(2, actualSuppliers.size());
            assertEquals(expectedSuppliers, actualSuppliers);
            verify(supplierRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no suppliers exist")
        void getAllSuppliers_WhenNoneExist_ShouldReturnEmptyList() {
            // Arrange
            when(supplierRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<Supplier> actualSuppliers = supplierService.getAllSuppliers();

            // Assert
            assertNotNull(actualSuppliers);
            assertTrue(actualSuppliers.isEmpty());
            verify(supplierRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Tests for registerSupplier")
    class RegisterSupplierTests {

        @Test
        @DisplayName("Should save supplier and return true when CNPJ is unique.")
        void registerSupplier_WithUniqueCnpj_ShouldSaveAndReturnTrue() {

            // Act
            boolean result = supplierService.registerSupplier(supplierDto);

            // Assert
            assertTrue(result);
            ArgumentCaptor<Supplier> supplierCaptor = ArgumentCaptor.forClass(Supplier.class);
            verify(supplierRepository, times(1)).save(supplierCaptor.capture());
            Supplier savedSupplier = supplierCaptor.getValue();
            assertEquals(supplierDto.name(), savedSupplier.getName());
            assertEquals(supplierDto.cnpj(), savedSupplier.getCnpj());
            assertEquals(supplierDto.email(), savedSupplier.getEmail());
            assertEquals(supplierDto.phoneNumber(), savedSupplier.getPhoneNumber());
        }

        @Test
        @DisplayName("Should not save supplier and throw exception when CNPJ already exists.")
        void registerSupplier_WithExistingCnpj_ShouldNotSaveAndThrowSupplierAlreadyExistsException() {
            when(supplierRepository.save(any())).thenThrow(new SupplierAlreadyExistsException(""));

            assertThrows(SupplierAlreadyExistsException.class, () -> {
                supplierService.registerSupplier(supplierDto);
            });
            verify(supplierRepository, times(1)).save(any(Supplier.class));
        }
    }

    @Nested
    @DisplayName("Tests for updateSupplier")
    class UpdateSupplierTests {

        @Test
        @DisplayName("Should update supplier and return true when supplier exists")
        void updateSupplier_WhenSupplierExists_ShouldUpdateAndReturnTrue() {
            // Arrange
            Supplier existingSupplier = new Supplier("Old Name", supplierDto.cnpj(), "old@email.com", "old-phone");
            when(supplierRepository.findByCnpj(supplierDto.cnpj())).thenReturn(Optional.of(existingSupplier));

            // Act
            boolean result = supplierService.updateSupplier(supplierDto);

            // Assert
            assertTrue(result);
            ArgumentCaptor<Supplier> supplierCaptor = ArgumentCaptor.forClass(Supplier.class);
            verify(supplierRepository, times(1)).save(supplierCaptor.capture());
            Supplier updatedSupplier = supplierCaptor.getValue();
            assertSame(existingSupplier, updatedSupplier);
            assertEquals(supplierDto.name(), updatedSupplier.getName());
            assertEquals(supplierDto.email(), updatedSupplier.getEmail());
            assertEquals(supplierDto.phoneNumber(), updatedSupplier.getPhoneNumber());
        }

        @Test
        @DisplayName("Should not update and throw exception when supplier does not exist")
        void updateSupplier_WhenSupplierDoesNotExist_ShouldNotUpdateAndThrowSupplierNotFoundException() {
            assertThrows(SupplierNotFoundException.class, () -> {
                supplierService.updateSupplier(supplierDto);
            });
            verify(supplierRepository, never()).save(any(Supplier.class));
        }
    }
}
package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.NotaFiscalRegisterDto;
import com.edu.ManagementPlayground.Dto.NotaFiscalResponseDto;
import com.edu.ManagementPlayground.Dto.NotaFiscalUpdateDto;
import com.edu.ManagementPlayground.Entity.NotaFiscal;
import com.edu.ManagementPlayground.Entity.Supplier;
import com.edu.ManagementPlayground.Enum.StorageContext;
import com.edu.ManagementPlayground.Repository.NotaFiscalRepository;
import com.edu.ManagementPlayground.Repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotaFiscalServiceTest {

    @Mock
    private NotaFiscalRepository notaFiscalRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private StorageService storageService;

    @InjectMocks
    private NotaFiscalService notaFiscalService;

    private NotaFiscalRegisterDto notaFiscalRegisterDto;
    private NotaFiscalUpdateDto notaFiscalUpdateDto;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        mockFile = new MockMultipartFile(
                "file",
                "nota.pdf",
                "application/pdf",
                "test data".getBytes()
        );

        notaFiscalRegisterDto = new NotaFiscalRegisterDto(
                "12345",
                LocalDate.now(),
                new BigDecimal("100.50"),
                1L,
                mockFile
        );

        notaFiscalUpdateDto = new NotaFiscalUpdateDto(
                "12345",
                LocalDate.now().plusDays(1),
                new BigDecimal("150.75"),
                2L,
                null
        );
    }
    @Nested
    @DisplayName("Tests for getNotaFiscal")
    class GetNotaFiscalTests{
        @Test
        @DisplayName("Should return a list of NotaFiscal.")
        void getAllNotaFiscal_ShouldReturnListOfDtos() {
            // Arrange
            List<NotaFiscalResponseDto> expectedDtos = List.of(
                    new NotaFiscalResponseDto(
                            "1231231231",
                            LocalDate.now(),
                            BigDecimal.TEN,
                            "file01.pdf",
                            1L));
            when(notaFiscalRepository.findAllWithSupplierId()).thenReturn(expectedDtos);

            // Act
            List<NotaFiscalResponseDto> actualDtos = notaFiscalService.getAllNotaFiscal();

            // Assert
            assertNotNull(actualDtos);
            assertEquals(1, actualDtos.size());
            assertEquals(expectedDtos, actualDtos);
            verify(notaFiscalRepository, times(1)).findAllWithSupplierId();
        }

        @Test
        @DisplayName("Should return a Resource for the NotaFiscal .PDF file.")
        void getNotaFiscalFile_ShouldReturnResourceFromStorageService() {
            // Arrange
            String fileReference = "some/path/nota.pdf";
            Resource mockResource = mock(Resource.class);
            when(storageService.loadAsResource(fileReference, StorageContext.NOTAFISCAL)).thenReturn(mockResource);

            // Act
            Resource result = notaFiscalService.getNotaFiscalFile(fileReference);

            // Assert
            assertNotNull(result);
            assertEquals(mockResource, result);
            verify(storageService, times(1)).loadAsResource(fileReference, StorageContext.NOTAFISCAL);
        }
    }

    @Nested
    @DisplayName("Tests for registerNotaFiscal")
    class RegisterNotaFiscalTests{
        @Test
        @DisplayName("Should persist a NotaFiscal that does not exists yet.")
        void registerNotaFiscal_WhenNotaFiscalDoesNotExist_ShouldSaveAndReturnTrue() {
            // Arrange
            String savedFilePath = "generated/path/nota.pdf";
            Supplier mockSupplier = new Supplier();
            when(notaFiscalRepository.existsByNumberIdentifier(notaFiscalRegisterDto.numberIdentifier())).thenReturn(false);
            when(supplierRepository.getReferenceById(notaFiscalRegisterDto.supplierId())).thenReturn(mockSupplier);
            when(storageService.storeFile(mockFile, StorageContext.NOTAFISCAL)).thenReturn(savedFilePath);

            // Act
            boolean result = notaFiscalService.registerNotaFiscal(notaFiscalRegisterDto);

            // Assert
            assertTrue(result);
            ArgumentCaptor<NotaFiscal> notaFiscalCaptor = ArgumentCaptor.forClass(NotaFiscal.class);
            verify(notaFiscalRepository, times(1)).save(notaFiscalCaptor.capture());
            NotaFiscal capturedNota = notaFiscalCaptor.getValue();
            assertEquals(notaFiscalRegisterDto.numberIdentifier(), capturedNota.getNumberIdentifier());
            assertEquals(notaFiscalRegisterDto.totalValue(), capturedNota.getTotalValue());
            assertEquals(savedFilePath, capturedNota.getFileReference());
            assertEquals(mockSupplier, capturedNota.getSupplier());
        }
        @Test
        @DisplayName("Should not save new NotaFiscal if its numberIdentifier already exists on db.")
        void registerNotaFiscal_WhenNotaFiscalAlreadyExists_ShouldNotSaveAndReturnFalse() {
            // Arrange
            when(notaFiscalRepository.existsByNumberIdentifier(notaFiscalRegisterDto.numberIdentifier())).thenReturn(true);

            // Act
            boolean result = notaFiscalService.registerNotaFiscal(notaFiscalRegisterDto);

            // Assert
            assertFalse(result);
            verify(supplierRepository, never()).getReferenceById(anyLong());
            verify(storageService, never()).storeFile(any(), any());
            verify(notaFiscalRepository, never()).save(any(NotaFiscal.class));
        }
        @Test
        @DisplayName("Should not save NotaFiscal when StorageService throws RuntimeException.")
        void registerNotaFiscal_WhenStorageServiceFails_ShouldNotSaveNotaFiscal() {
            // Arrange
            when(notaFiscalRepository.existsByNumberIdentifier(anyString())).thenReturn(false);
            when(supplierRepository.getReferenceById(anyLong())).thenReturn(new Supplier());
            when(storageService.storeFile(any(), any())).thenThrow(new RuntimeException("Disk is full!"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> {
                notaFiscalService.registerNotaFiscal(notaFiscalRegisterDto);
            });
            verify(notaFiscalRepository, never()).save(any(NotaFiscal.class));
        }
    }

    @Nested
    @DisplayName("Tests for updateNotaFiscal")
    class UpdateNotaFiscalTests{
        @Test
        @DisplayName("Should update NotaFiscal attributes only when it exists on db.")
        void updateNotaFiscal_WhenNotaFiscalExists_ShouldUpdateFields() {
            // Arrange
            NotaFiscal existingNotaFiscal = new NotaFiscal("12345", LocalDate.now(), BigDecimal.TEN, "old/path.pdf", new Supplier());
            Supplier newSupplier = new Supplier();
            when(notaFiscalRepository.findByNumberIdentifier(notaFiscalUpdateDto.numberIdentifier())).thenReturn(Optional.of(existingNotaFiscal));
            when(supplierRepository.getReferenceById(notaFiscalUpdateDto.supplierId())).thenReturn(newSupplier);

            // Act
            notaFiscalService.updateNotaFiscal(notaFiscalUpdateDto);

            // Assert
            verify(notaFiscalRepository, times(1)).saveAndFlush(any(NotaFiscal.class));
            verify(storageService, never()).updateFile(any(), any(), any());
            assertEquals(notaFiscalUpdateDto.issueDate(), existingNotaFiscal.getIssueDate());
            assertEquals(notaFiscalUpdateDto.totalValue(), existingNotaFiscal.getTotalValue());
            assertEquals(newSupplier, existingNotaFiscal.getSupplier());
        }
        @Test
        @DisplayName("Should call file exchange for given NotaFiscal.")
        void updateNotaFiscal_WhenNewFileIsProvided_ShouldCallUpdateFile() {
            // Arrange
            NotaFiscalUpdateDto updateDtoWithFile = new NotaFiscalUpdateDto(
                    "12345", LocalDate.now(), BigDecimal.ONE, 1L, mockFile
            );
            NotaFiscal existingNotaFiscal = new NotaFiscal("12345", LocalDate.now(), BigDecimal.TEN, "old/path.pdf", new Supplier());
            when(notaFiscalRepository.findByNumberIdentifier(updateDtoWithFile.numberIdentifier())).thenReturn(Optional.of(existingNotaFiscal));
            when(supplierRepository.getReferenceById(anyLong())).thenReturn(new Supplier());

            // Act
            notaFiscalService.updateNotaFiscal(updateDtoWithFile);

            // Assert
            verify(storageService, times(1)).updateFile(eq(mockFile), any(), eq(StorageContext.NOTAFISCAL));
            verify(notaFiscalRepository, times(1)).saveAndFlush(any(NotaFiscal.class));
        }
        @Test
        @DisplayName("Should throw EntityNotFoundException.")
        void updateNotaFiscal_WhenNotaFiscalNotFound_ShouldThrowEntityNotFoundException() {
            // Arrange
            when(notaFiscalRepository.findByNumberIdentifier(notaFiscalUpdateDto.numberIdentifier())).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                notaFiscalService.updateNotaFiscal(notaFiscalUpdateDto);
            });
            verify(notaFiscalRepository, never()).saveAndFlush(any());
            verify(storageService, never()).updateFile(any(), any(), any());
        }
    }


}
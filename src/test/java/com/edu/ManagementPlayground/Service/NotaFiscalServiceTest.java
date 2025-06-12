package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.NotaFiscalRegisterDto;
import com.edu.ManagementPlayground.Dto.NotaFiscalResponseDto;
import com.edu.ManagementPlayground.Dto.NotaFiscalUpdateDto;
import com.edu.ManagementPlayground.Entity.NotaFiscal;
import com.edu.ManagementPlayground.Entity.Supplier;
import com.edu.ManagementPlayground.Enum.StorageContext;
import com.edu.ManagementPlayground.Exception.NotaFiscalAlreadyExistsException;
import com.edu.ManagementPlayground.Exception.NotaFiscalNotFoundException;
import com.edu.ManagementPlayground.Repository.NotaFiscalRepository;
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
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotaFiscalServiceTest {

    @Mock
    private NotaFiscalRepository notaFiscalRepository;
    @Mock
    private SupplierService supplierService;
    @Mock
    private StorageService storageService;

    @InjectMocks
    private com.edu.ManagementPlayground.Service.NotaFiscalService notaFiscalService;

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
        @DisplayName("Should return a set of NotaFiscal.")
        void getAllNotaFiscal_ShouldReturnSetOfDtos() {
            // Arrange
            Set<NotaFiscalResponseDto> expectedDtos = Set.of(
                    new NotaFiscalResponseDto(
                            1L,
                            "1231231231",
                            LocalDate.now(),
                            BigDecimal.TEN,
                            "file01.pdf",
                            1L));
            when(notaFiscalRepository.findAllWithSupplierId()).thenReturn(expectedDtos);

            // Act
            Set<NotaFiscalResponseDto> actualDtos = notaFiscalService.getAllNotasFiscais();

            // Assert
            assertNotNull(actualDtos);
            assertEquals(1, actualDtos.size());
            assertEquals(expectedDtos, actualDtos);
            verify(notaFiscalRepository, times(1)).findAllWithSupplierId();
        }

        @Test
        @DisplayName("Should return a Resource for the NotaFiscal PDF file.")
        void getNotaFiscalFile_ShouldReturnResourceFromStorageService() {
            // Arrange
            String fileReference = "notafiscal01.pdf";
            Resource mockResource = mock(Resource.class);
            when(storageService.loadAsResource(fileReference, StorageContext.NOTAFISCAL)).thenReturn(mockResource);

            // Act
            Resource result = notaFiscalService.getNotaFiscalFile(fileReference);

            // Assert
            assertNotNull(result);
            assertEquals(mockResource, result);
            verify(storageService, times(1)).loadAsResource(fileReference, StorageContext.NOTAFISCAL);
        }

        @Test
        @DisplayName("Should return a Single Nota Fiscal")
        void getSingleNotaFiscal_ShouldReturnOneDto(){
            Supplier supplierDummy = new Supplier();
            supplierDummy.setId(1L);
            NotaFiscal notaFiscalDummy = new NotaFiscal("123", LocalDate.now(), BigDecimal.TEN, "notafiscal01.pdf", supplierDummy);
            when(notaFiscalRepository.findById(1L)).thenReturn(Optional.of(notaFiscalDummy));

            NotaFiscalResponseDto serviceReturn = notaFiscalService.getNotaFiscal(1L);

            assertNotNull(serviceReturn);
            verify(notaFiscalRepository, times(1)).findById(anyLong());
            assertEquals(notaFiscalDummy.getId(), serviceReturn.id());
        }

        @Test
        @DisplayName("Should throw NotaFiscalNotFoundException")
        void getSingleComprovantePagamento_ShouldThrowComprovantePagamentoNotFoundException(){
            when(notaFiscalRepository.findById(1L)).thenThrow(new NotaFiscalNotFoundException(""));

            assertThrows(NotaFiscalNotFoundException.class, () -> {
                        notaFiscalService.getNotaFiscal(1L);
                    }
            );
        }
    }

    @Nested
    @DisplayName("Tests for registerNotaFiscal")
    class RegisterNotaFiscalTests{
        @Test
        @DisplayName("Should persist a NotaFiscal that does not exists yet.")
        void registerNotaFiscal_WhenNotaFiscalDoesNotExist_ShouldSaveAndReturnTrue() {
            // Arrange
            String fileReference = "notafiscal01.pdf";
            Supplier mockSupplier = new Supplier();
            when(supplierService.getSupplierReference(notaFiscalRegisterDto.supplierId())).thenReturn(mockSupplier);
            when(storageService.storeFile(mockFile, StorageContext.NOTAFISCAL)).thenReturn(fileReference);

            // Act
            String result = notaFiscalService.createNotaFiscal(notaFiscalRegisterDto);

            // Assert
            assertNotNull(result);
            ArgumentCaptor<NotaFiscal> notaFiscalCaptor = ArgumentCaptor.forClass(NotaFiscal.class);
            verify(notaFiscalRepository, times(1)).save(notaFiscalCaptor.capture());
            NotaFiscal capturedNota = notaFiscalCaptor.getValue();
            assertEquals(notaFiscalRegisterDto.numberIdentifier(), capturedNota.getNumberIdentifier());
            assertEquals(notaFiscalRegisterDto.totalValue(), capturedNota.getTotalValue());
            assertEquals(fileReference, capturedNota.getFileReference());
            assertEquals(mockSupplier, capturedNota.getSupplier());
        }
        @Test
        @DisplayName("Should not save new NotaFiscal if its ID already exists on db.")
        void registerNotaFiscal_WhenNotaFiscalAlreadyExists_ShouldThrowNotaFiscalAlreadyExistsException() {
            // Arrange
            Supplier dummySupplier = new Supplier();
            when(supplierService.getSupplierReference(1L)).thenReturn(dummySupplier);
            when(storageService.storeFile(any(), any())).thenReturn("notafiscal01.pdf");
            when(notaFiscalRepository.save(any())).thenThrow(new NotaFiscalAlreadyExistsException(""));

            assertThrows(NotaFiscalAlreadyExistsException.class, () -> {
                notaFiscalService.createNotaFiscal(notaFiscalRegisterDto);
            });
            verify(supplierService, times(1)).getSupplierReference(anyLong());
            verify(storageService, times(1)).storeFile(any(), any());
            verify(notaFiscalRepository, times(1)).save(any(NotaFiscal.class));
            verify(storageService, times(1)).deleteFile(any(), any());
        }
        @Test
        @DisplayName("Should not save NotaFiscal when StorageService throws RuntimeException.")
        void registerNotaFiscal_WhenStorageServiceFails_ShouldNotSaveNotaFiscal() {
            // Arrange
            when(supplierService.getSupplierReference(anyLong())).thenReturn(new Supplier());
            when(storageService.storeFile(any(), any())).thenThrow(new RuntimeException("Disk is full!"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> {
                notaFiscalService.createNotaFiscal(notaFiscalRegisterDto);
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
            when(supplierService.getSupplierReference(notaFiscalUpdateDto.supplierId())).thenReturn(newSupplier);

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
            when(supplierService.getSupplierReference(anyLong())).thenReturn(new Supplier());

            // Act
            notaFiscalService.updateNotaFiscal(updateDtoWithFile);

            // Assert
            verify(storageService, times(1)).updateFile(eq(mockFile), any(), eq(StorageContext.NOTAFISCAL));
            verify(notaFiscalRepository, times(1)).saveAndFlush(any(NotaFiscal.class));
        }
        @Test
        @DisplayName("Should throw NotaFiscalNotFoundException.")
        void updateNotaFiscal_WhenNotaFiscalNotFound_ShouldThrowNotaFiscalNotFoundException() {
            // Arrange
            when(notaFiscalRepository.findByNumberIdentifier(any())).thenThrow(new NotaFiscalNotFoundException(""));

            assertThrows(NotaFiscalNotFoundException.class, () -> {
                notaFiscalService.updateNotaFiscal(notaFiscalUpdateDto);
            });
            verify(notaFiscalRepository, never()).saveAndFlush(any());
            verify(storageService, never()).updateFile(any(), any(), any());
        }
    }
}
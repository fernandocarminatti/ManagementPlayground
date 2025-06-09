package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.BoletoRegisterDto;
import com.edu.ManagementPlayground.Dto.BoletoResponseDto;
import com.edu.ManagementPlayground.Dto.BoletoUpdateDto;
import com.edu.ManagementPlayground.Entity.Boleto;
import com.edu.ManagementPlayground.Entity.NotaFiscal;
import com.edu.ManagementPlayground.Enum.StorageContext;
import com.edu.ManagementPlayground.Repository.BoletoRepository;
import com.edu.ManagementPlayground.Repository.NotaFiscalRepository;
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
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoletoServiceTest {

    @Mock
    private NotaFiscalRepository notaFiscalRepository;
    @Mock
    private StorageService storageService;
    @Mock
    private BoletoRepository boletoRepository;
    @Mock
    private Boleto existingBoleto;

    @InjectMocks
    private BoletoService boletoService;

    private BoletoRegisterDto boletoRegisterDto;
    private BoletoUpdateDto boletoUpdateDto;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        mockFile = new MockMultipartFile(
                "File01",
                "file01.pdf",
                "application/pdf",
                "test".getBytes()
        );

        boletoRegisterDto = new BoletoRegisterDto(
                "123",
                LocalDate.now().plusDays(1),
                BigDecimal.TEN,
                1,
                "1234567890",
                mockFile
        );

        boletoUpdateDto = new BoletoUpdateDto(
                "456",
                LocalDate.now().plusDays(10),
                BigDecimal.TEN,
                2,
                "1234567890",
                mockFile
        );
    }

    @Nested
    @DisplayName("Tests for getBoleto")
    class GetBoletos{
        @Test
        @DisplayName("Should return a Set of Boletos.")
        void getAllBoletos_ShouldReturnSetOfDtos(){
            // Arrange
            Set<BoletoResponseDto> expectedDtos = Set.of(
                    new BoletoResponseDto(
                            "1231231231",
                            LocalDate.now().plusDays(10),
                            BigDecimal.TEN,
                            1,
                            "file01.pdf"));
            when(boletoRepository.findAllProjection()).thenReturn(expectedDtos);

            // Act
            Set<BoletoResponseDto> actualDtos = boletoService.getAllBoletos();

            // Assert
            assertNotNull(actualDtos);
            assertEquals(1, actualDtos.size());
            assertEquals(expectedDtos, actualDtos);
            verify(boletoRepository, times(1)).findAllProjection();
        }

        @Test
        @DisplayName("Should return a Resource for the Boleto PDF file.")
        void getNotaFiscalFile_ShouldReturnResourceFromStorageService() {
            // Arrange
            String fileReference = "uploads/boleto01.pdf";
            Resource mockResource = mock(Resource.class);
            when(storageService.loadAsResource(fileReference, StorageContext.BOLETO)).thenReturn(mockResource);

            // Act
            Resource result = boletoService.getBoletoFile(fileReference);

            // Assert
            assertNotNull(result);
            assertEquals(mockResource, result);
            verify(storageService, times(1)).loadAsResource(fileReference, StorageContext.BOLETO);
        }
    }

    @Nested
    @DisplayName("Tests for registerBoleto")
    class RegisterBoletoTests{
        @Test
        @DisplayName("Should persist a Boleto that does not exists yet.")
        void registerBoleto_WhenBoletoDoesNotExist_ShouldSaveAndReturnTrue() {
            // Arrange
            String savedFilePath = "upload/boleto01.pdf";
            NotaFiscal mockNotaFiscal = new NotaFiscal();
            when(boletoRepository.existsByTypeableLine(boletoRegisterDto.typeableLine())).thenReturn(false);
            when(notaFiscalRepository.getReferenceByNumberIdentifier(boletoRegisterDto.notaFiscalNumberIdentifier())).thenReturn(mockNotaFiscal);
            when(storageService.storeFile(mockFile, StorageContext.BOLETO)).thenReturn(savedFilePath);

            // Act
            boolean result = boletoService.registerBoleto(boletoRegisterDto);

            // Assert
            assertTrue(result);
            ArgumentCaptor<Boleto> boletoCaptor = ArgumentCaptor.forClass(Boleto.class);
            verify(boletoRepository, times(1)).save(boletoCaptor.capture());
            Boleto capturedBoleto = boletoCaptor.getValue();
            assertEquals(boletoRegisterDto.typeableLine(), capturedBoleto.getTypeableLine());
            assertEquals(boletoRegisterDto.value(), capturedBoleto.getValue());
            assertEquals(savedFilePath, capturedBoleto.getFileReference());
            assertEquals(mockNotaFiscal, capturedBoleto.getNotaFiscal());
        }
        @Test
        @DisplayName("Should not save new Boleto if its Typeable Line already exists on db.")
        void registerBoleto_WhenBoletoAlreadyExists_ShouldNotSaveAndReturnFalse() {
            // Arrange
            when(boletoRepository.existsByTypeableLine(boletoRegisterDto.typeableLine())).thenReturn(true);

            // Act
            boolean result = boletoService.registerBoleto(boletoRegisterDto);

            // Assert
            assertFalse(result);
            verify(notaFiscalRepository, never()).getReferenceById(anyLong());
            verify(storageService, never()).storeFile(any(), any());
            verify(notaFiscalRepository, never()).save(any(NotaFiscal.class));
        }
        @Test
        @DisplayName("Should not save Boleto when StorageService throws RuntimeException.")
        void registerBoleto_WhenStorageServiceFails_ShouldNotSaveBoleto() {
            // Arrange
            when(boletoRepository.existsByTypeableLine(anyString())).thenReturn(false);
            when(notaFiscalRepository.getReferenceByNumberIdentifier(anyString())).thenReturn(new NotaFiscal());
            when(storageService.storeFile(any(), any())).thenThrow(new RuntimeException("Disk is full!"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> {
                boletoService.registerBoleto(boletoRegisterDto);
            });
            verify(notaFiscalRepository, never()).save(any(NotaFiscal.class));
        }
    }

    @Nested
    @DisplayName("Tests for updateBoleto")
    class UpdateBoletoTests{
        @Test
        @DisplayName("Should update boleto successfully when boleto exists and a new file is provided")
        void updateBoleto_whenBoletoExistsAndFileProvided_shouldUpdateSuccessfully() {
            // Arrange
            Boleto existingBoleto = new Boleto();
            existingBoleto.setTypeableLine("123456789");
            existingBoleto.setFileReference("uploads/boleto01.pdf");
            existingBoleto.setValue(new BigDecimal("200.00"));
            NotaFiscal notaFiscalReference = new NotaFiscal();
            notaFiscalReference.setNumberIdentifier("NF-002");

            when(boletoRepository.findByTypeableLine(boletoUpdateDto.typeableLine())).thenReturn(Optional.of(existingBoleto));
            when(notaFiscalRepository.getReferenceByNumberIdentifier(boletoUpdateDto.notaFiscalNumberIdentifier())).thenReturn(notaFiscalReference);

            // Act
            boletoService.updateBoleto(boletoUpdateDto);

            // Assert
            verify(storageService).updateFile(eq(mockFile), eq(Path.of(existingBoleto.getFileReference())), eq(StorageContext.BOLETO));
            ArgumentCaptor<Boleto> boletoCaptor = ArgumentCaptor.forClass(Boleto.class);
            verify(boletoRepository).saveAndFlush(boletoCaptor.capture());
            Boleto capturedBoleto = boletoCaptor.getValue();

            assertThat(capturedBoleto.getValue()).isEqualTo(boletoUpdateDto.value());
            assertThat(capturedBoleto.getDueDate()).isEqualTo(boletoUpdateDto.dueDate());
            assertThat(capturedBoleto.getPaymentStatus()).isEqualTo(boletoUpdateDto.paymentStatus());
            assertThat(capturedBoleto.getNotaFiscal()).isEqualTo(notaFiscalReference);
        }
        @Test
        @DisplayName("Should update boleto successfully when no new file is provided")
        void updateBoleto_whenFileIsNull_shouldUpdateWithoutCallingStorageService() {
            // Arrange
            NotaFiscal dummyNF = new NotaFiscal();
            Boleto existingBoleto = new Boleto();
            BoletoUpdateDto withNullFile = new BoletoUpdateDto("123", LocalDate.now().plusDays(10), BigDecimal.ONE, 1, "456", null);
            when(boletoRepository.findByTypeableLine(withNullFile.typeableLine())).thenReturn(Optional.of(existingBoleto));
            when(notaFiscalRepository.getReferenceByNumberIdentifier(withNullFile.notaFiscalNumberIdentifier())).thenReturn(dummyNF);

            // Act
            boletoService.updateBoleto(withNullFile);

            // Assert
            verify(storageService, never()).updateFile(any(), any(), any());
            verify(boletoRepository).saveAndFlush(any(Boleto.class));
        }
        @Test
        @DisplayName("Should call file exchange for given Boleto.")
        void updateBoleto_WhenNewFileIsProvided_ShouldCallUpdateFile() {
            // Arrange
            NotaFiscal emptyNF = new NotaFiscal();
            Boleto existingBoleto = new Boleto("12345", LocalDate.now(), BigDecimal.TEN, 1, "boleto01.pdf", emptyNF);
            when(boletoRepository.findByTypeableLine(boletoUpdateDto.typeableLine())).thenReturn(Optional.of(existingBoleto));
            when(notaFiscalRepository.getReferenceByNumberIdentifier(anyString())).thenReturn(emptyNF);

            // Act
            boletoService.updateBoleto(boletoUpdateDto);

            // Assert
            verify(storageService, times(1)).updateFile(eq(mockFile), any(), eq(StorageContext.BOLETO));
            verify(boletoRepository, times(1)).saveAndFlush(any(Boleto.class));
        }
        @Test
        @DisplayName("Should throw EntityNotFoundException.")
        void updateBoleto_WhenBoletoNotFound_ShouldThrowEntityNotFoundException() {
            // Arrange
            when(boletoRepository.findByTypeableLine(boletoUpdateDto.typeableLine())).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                boletoService.updateBoleto(boletoUpdateDto);
            });
            verify(boletoRepository, never()).saveAndFlush(any());
            verify(storageService, never()).updateFile(any(), any(), any());
        }
    }
}
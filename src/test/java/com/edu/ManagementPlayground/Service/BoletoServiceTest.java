package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.BoletoRegisterDto;
import com.edu.ManagementPlayground.Dto.BoletoResponseDto;
import com.edu.ManagementPlayground.Dto.BoletoUpdateDto;
import com.edu.ManagementPlayground.Entity.Boleto;
import com.edu.ManagementPlayground.Entity.NotaFiscal;
import com.edu.ManagementPlayground.Enum.StorageContext;
import com.edu.ManagementPlayground.Exception.BoletoAlreadyExistsException;
import com.edu.ManagementPlayground.Exception.BoletoNotFoundException;
import com.edu.ManagementPlayground.Repository.BoletoRepository;
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
    private NotaFiscalService notaFiscalService;
    @Mock
    private StorageService storageService;
    @Mock
    private BoletoRepository boletoRepository;

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
                1,
                mockFile
        );

        boletoUpdateDto = new BoletoUpdateDto(
                "456",
                LocalDate.now().plusDays(10),
                BigDecimal.TEN,
                2,
                2,
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
                            1L,
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
        void getBoleto_ShouldReturnResourceFromStorageService() {
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

        @Test
        @DisplayName("Should return a Single Boleto")
        void getSingleBoleto_ShouldReturnOneDto(){
            NotaFiscal notaFiscalDummy = new NotaFiscal();
            notaFiscalDummy.setId(1L);
            Boleto boletoDummy = new Boleto("123", LocalDate.now(), BigDecimal.TEN, 1, "boleto01.pdf", notaFiscalDummy);
            when(boletoRepository.findById(1L)).thenReturn(Optional.of(boletoDummy));

            BoletoResponseDto serviceReturn = boletoService.getBoleto(1L);

            assertNotNull(serviceReturn);
            verify(boletoRepository, times(1)).findById(anyLong());
            assertEquals(boletoDummy.getId(), serviceReturn.id());
        }

        @Test
        @DisplayName("Should throw BoletoNotFoundException")
        void getSingleBoleto_ShouldThrowBoletoNotFoundException(){
            when(boletoRepository.findById(1L)).thenThrow(new BoletoNotFoundException(""));

            assertThrows(BoletoNotFoundException.class, () -> {
                        boletoService.getBoleto(1L);
                    }
            );
        }
    }

    @Nested
    @DisplayName("Tests for registerBoleto")
    class RegisterBoletoTests{
        @Test
        @DisplayName("Should persist a Boleto that does not exists yet.")
        void registerBoleto_WhenBoletoDoesNotExist_ShouldSaveAndReturnTrue() {
            // Arrange
            NotaFiscal notaFiscalDummy = new NotaFiscal();
            notaFiscalDummy.setId(1L);
            when(notaFiscalService.getNotaFiscalReference(boletoRegisterDto.notaFiscalId())).thenReturn(notaFiscalDummy);
            when(storageService.storeFile(any(), any())).thenReturn("boleto01.pdf");

            // Act
            String result = boletoService.createBoleto(boletoRegisterDto);

            // Assert
            assertNotNull(result);
            ArgumentCaptor<Boleto> boletoCaptor = ArgumentCaptor.forClass(Boleto.class);
            verify(boletoRepository, times(1)).save(boletoCaptor.capture());
            Boleto capturedBoleto = boletoCaptor.getValue();
            assertEquals(boletoRegisterDto.typeableLine(), capturedBoleto.getTypeableLine());
            assertEquals(boletoRegisterDto.value(), capturedBoleto.getValue());
            assertEquals("boleto01.pdf", capturedBoleto.getFileReference());
            assertEquals(notaFiscalDummy, capturedBoleto.getNotaFiscal());
        }
        @Test
        @DisplayName("Should not save new Boleto if its Typeable Line already exists on db.")
        void registerBoleto_WhenBoletoAlreadyExists_ShouldThrowBoletoAlreadyExistsException() {
            // Arrange
            NotaFiscal notaFiscalDummy = new NotaFiscal();
            notaFiscalDummy.setId(1L);
            when(notaFiscalService.getNotaFiscalReference(1L)).thenReturn(notaFiscalDummy);
            when(storageService.storeFile(any(), any())).thenReturn("boleto0.pdf");
            when(boletoRepository.save(any())).thenThrow(new BoletoAlreadyExistsException(""));

            assertThrows(BoletoAlreadyExistsException.class, () -> {
                boletoService.createBoleto(boletoRegisterDto);
            });
            verify(notaFiscalService, times(1)).getNotaFiscalReference(anyLong());
            verify(storageService, times(1)).storeFile(any(), any());
            verify(boletoRepository, times(1)).save(any(Boleto.class));
            verify(storageService, times(1)).deleteFile(any(), any());
        }
        @Test
        @DisplayName("Should not save Boleto when StorageService throws RuntimeException.")
        void registerBoleto_WhenStorageServiceFails_ShouldNotSaveBoleto() {
            // Arrange
            when(notaFiscalService.getNotaFiscalReference(anyLong())).thenReturn(new NotaFiscal());
            when(storageService.storeFile(any(), any())).thenThrow(new RuntimeException("Disk is full!"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> {
                boletoService.createBoleto(boletoRegisterDto);
            });
            verify(boletoRepository, never()).save(any(Boleto.class));
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
            existingBoleto.setFileReference("boleto01.pdf");
            existingBoleto.setValue(new BigDecimal("200.00"));
            NotaFiscal notaFiscalReference = new NotaFiscal();
            notaFiscalReference.setNumberIdentifier("NF-002");

            when(boletoRepository.findByTypeableLine(boletoUpdateDto.typeableLine())).thenReturn(Optional.of(existingBoleto));
            when(notaFiscalService.getNotaFiscalReference(boletoUpdateDto.notaFiscalId())).thenReturn(notaFiscalReference);

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
            BoletoUpdateDto withNullFile = new BoletoUpdateDto("123", LocalDate.now().plusDays(10), BigDecimal.ONE, 1, 456, null);
            when(boletoRepository.findByTypeableLine(withNullFile.typeableLine())).thenReturn(Optional.of(existingBoleto));
            when(notaFiscalService.getNotaFiscalReference(withNullFile.notaFiscalId())).thenReturn(dummyNF);

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
            when(notaFiscalService.getNotaFiscalReference(anyLong())).thenReturn(emptyNF);

            // Act
            boletoService.updateBoleto(boletoUpdateDto);

            // Assert
            verify(storageService, times(1)).updateFile(eq(mockFile), any(), eq(StorageContext.BOLETO));
            verify(boletoRepository, times(1)).saveAndFlush(any(Boleto.class));
        }
        @Test
        @DisplayName("Should throw BoletoNotFoundException.")
        void updateBoleto_WhenBoletoNotFound_ShouldThrowBoletoNotFoundException() {
            // Arrange
            when(boletoRepository.findByTypeableLine(any())).thenThrow(new BoletoNotFoundException(""));

            assertThrows(BoletoNotFoundException.class, () -> {
                boletoService.updateBoleto(boletoUpdateDto);
            });
            verify(boletoRepository, never()).saveAndFlush(any());
            verify(storageService, never()).updateFile(any(), any(), any());
        }
    }
}
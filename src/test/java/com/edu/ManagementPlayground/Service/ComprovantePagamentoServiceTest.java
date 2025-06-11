package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.ComprovantePagamentoRegisterDto;
import com.edu.ManagementPlayground.Dto.ComprovantePagamentoResponseDto;
import com.edu.ManagementPlayground.Dto.ComprovantePagamentoUpdateDto;
import com.edu.ManagementPlayground.Entity.Boleto;
import com.edu.ManagementPlayground.Entity.ComprovantePagamento;
import com.edu.ManagementPlayground.Enum.StorageContext;
import com.edu.ManagementPlayground.Exception.ComprovantePagamentoAlreadyExistsException;
import com.edu.ManagementPlayground.Exception.ComprovantePagamentoNotFoundException;
import com.edu.ManagementPlayground.Repository.ComprovantePagamentoRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComprovantePagamentoServiceTest {
    @Mock
    private BoletoService boletoService;
    @Mock
    private StorageService storageService;
    @Mock
    private ComprovantePagamentoRepository comprovantePagamentoRepository;

    @InjectMocks
    private ComprovantePagamentoService comprovantePagamentoService;

    private ComprovantePagamentoRegisterDto comprovantePagamentoRegisterDto;
    private ComprovantePagamentoUpdateDto comprovantePagamentoUpdateDto;
    private ComprovantePagamentoResponseDto comprovantePagamentoResponseDto;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp(){
        mockFile = new MockMultipartFile(
                "File01",
                "file01.pdf",
                "application/pdf",
                "test".getBytes()
        );

        comprovantePagamentoRegisterDto = new ComprovantePagamentoRegisterDto(
                LocalDate.now(),
                BigDecimal.TEN,
                1L,
                mockFile
        );

        comprovantePagamentoUpdateDto = new ComprovantePagamentoUpdateDto(
                LocalDate.now(),
                BigDecimal.TEN,
                1L,
                mockFile
        );

        comprovantePagamentoResponseDto = new ComprovantePagamentoResponseDto(
                1L,
                LocalDate.now(),
                BigDecimal.TEN,
                1L,
                "uploads/comprovantes/comprovante01.pdf"
        );
    }

    @Nested
    @DisplayName("Tests for getComprovantePagamento")
    class GetComprovantePagamento{
        @Test
        @DisplayName("Should return a Set of ComprovantePagamento.")
        void getAllComprovantePagamento_ShouldReturnSetOfDtos(){
            // Arrange
            Set<ComprovantePagamentoResponseDto> testSet = Set.of(comprovantePagamentoResponseDto);
            when(comprovantePagamentoRepository.findAllProjection()).thenReturn(testSet);

            // Act
            Set<ComprovantePagamentoResponseDto> setOfDtosReturned = comprovantePagamentoRepository.findAllProjection();

            // Assert
            assertNotNull(setOfDtosReturned);
            assertEquals(1, setOfDtosReturned.size());
            assertEquals(testSet, setOfDtosReturned);
            verify(comprovantePagamentoRepository, times(1)).findAllProjection();
        }

        @Test
        @DisplayName("Should return a Resource for the Comprovante PDF file.")
        void getComprovantePagamentoFile_ShouldReturnResourceFromStorageService() {
            // Arrange
            Resource mockResource = mock(Resource.class);
            when(storageService.loadAsResource(comprovantePagamentoResponseDto.fileReference(), StorageContext.COMPROVANTEPAGAMENTO)).thenReturn(mockResource);

            // Act
            Resource result = comprovantePagamentoService.getComprovantePagamentoFile(comprovantePagamentoResponseDto.fileReference());

            // Assert
            assertNotNull(result);
            assertEquals(mockResource, result);
            verify(storageService, times(1)).loadAsResource(comprovantePagamentoResponseDto.fileReference(), StorageContext.COMPROVANTEPAGAMENTO);
        }

        @Test
        @DisplayName("Should return a Single ComprovantePagamento")
        void getSingleComprovantePagamento_ShouldReturnOneDto(){
            Boleto boletoDummy = new Boleto();
            boletoDummy.setId(1L);
            ComprovantePagamento comprovanteDummy = new ComprovantePagamento(LocalDate.now(), BigDecimal.TEN, "uploads/comprovante/comprovante01.pdf", boletoDummy);
            when(comprovantePagamentoRepository.findById(1L)).thenReturn(Optional.of(comprovanteDummy));

            ComprovantePagamentoResponseDto serviceReturn = comprovantePagamentoService.getComprovante(1L);

            assertNotNull(serviceReturn);
            verify(comprovantePagamentoRepository, times(1)).findById(anyLong());
            assertEquals(comprovanteDummy.getId(), serviceReturn.id());
        }

        @Test
        @DisplayName("Should throw ComprovantePagamentoNotFoundException")
        void getSingleComprovantePagamento_ShouldThrowComprovantePagamentoNotFoundException(){
            when(comprovantePagamentoRepository.findById(1L)).thenThrow(new ComprovantePagamentoNotFoundException(""));

            assertThrows(ComprovantePagamentoNotFoundException.class, () -> {
                    comprovantePagamentoService.getComprovante(1L);
                }
            );
        }
    }

    @Nested
    @DisplayName("Tests for registerComprovantePagamento")
    class RegisterComprovantePagamento{
        @Test
        @DisplayName("Should persist a Comprovante Pagamento that does not exists yet.")
        void registerComprovante_WhenComprovanteDoesNotExist_ShouldSaveAndReturnTrue() {
            // Arrange
            Boleto boletoDummy = new Boleto();
            boletoDummy.setId(1L);
            when(boletoService.getBoletoReference(comprovantePagamentoRegisterDto.boletoId())).thenReturn(boletoDummy);
            when(storageService.storeFile(any(), any())).thenReturn("comprovante01.pdf");

            // Act
            String result = comprovantePagamentoService.registerComprovantePagamento(comprovantePagamentoRegisterDto);

            // Assert
            assertNotNull(result);
            ArgumentCaptor<ComprovantePagamento> comprovantePagamentoCaptor = ArgumentCaptor.forClass(ComprovantePagamento.class);
            verify(comprovantePagamentoRepository, times(1)).save(comprovantePagamentoCaptor.capture());
            ComprovantePagamento capturedComprovantePagamento = comprovantePagamentoCaptor.getValue();
            assertEquals(comprovantePagamentoRegisterDto.paymentDate(), capturedComprovantePagamento.getPaymentDate());
            assertEquals(comprovantePagamentoRegisterDto.value(), capturedComprovantePagamento.getValue());
            assertEquals("comprovante01.pdf", capturedComprovantePagamento.getFileReference());
            assertEquals(boletoDummy, capturedComprovantePagamento.getBoleto());
        }
        @Test
        @DisplayName("Should not save new Comprovante Pagamento if exists and delete new file from disk.")
        void registerComprovantePagamento_WhenComprovantePagamentoAlreadyExists_ShouldThrowComprovantePagamentoAlreadyExistsException() {
            Boleto boletoDummy = new Boleto();
            boletoDummy.setId(1L);
            when(boletoService.getBoletoReference(1L)).thenReturn(boletoDummy);
            when(storageService.storeFile(any(), any())).thenReturn("comprovantepag01.pdf");
            when(comprovantePagamentoRepository.save(any())).thenThrow(new ComprovantePagamentoAlreadyExistsException(""));

            assertThrows(ComprovantePagamentoAlreadyExistsException.class, () -> {
                comprovantePagamentoService.registerComprovantePagamento(comprovantePagamentoRegisterDto);
            });
            verify(boletoService, times(1)).getBoletoReference(anyLong());
            verify(storageService, times(1)).storeFile(any(), any());
            verify(comprovantePagamentoRepository, times(1)).save(any(ComprovantePagamento.class));
            verify(storageService, times(1)).deleteFile(any(), any());
        }
        @Test
        @DisplayName("Should not save Comprovante Pagamento when StorageService throws RuntimeException.")
        void registerComprovantePagamento_WhenStorageServiceFails_ShouldNotSave() {
            when(boletoService.getBoletoReference(anyLong())).thenReturn(new Boleto());
            when(storageService.storeFile(any(), any())).thenThrow(new RuntimeException("Disk is full!"));

            assertThrows(RuntimeException.class, () -> {
                comprovantePagamentoService.registerComprovantePagamento(comprovantePagamentoRegisterDto);
            });
            verify(comprovantePagamentoRepository, never()).save(any(ComprovantePagamento.class));
        }
    }

    @Nested
    @DisplayName("Tests for updateComprovantePagamento")
    class UpdateComprovantePagamento {
        @Test
        @DisplayName("Should update Comprovante Pagamento successfully when exists and a new file is provided")
        void updateComprovantePagamento_whenComprovantePagamentoExistsAndFileProvided_shouldUpdateSuccessfully() {
            Boleto existingBoleto = new Boleto();
            existingBoleto.setId(2L);
            ComprovantePagamento comprovantePagDummy = new ComprovantePagamento(
                    LocalDate.now(),
                    BigDecimal.TEN,
                    "comprovPag01.pdf",
                    existingBoleto
            );

            when(comprovantePagamentoRepository.findByBoletoId(anyLong())).thenReturn(Optional.of(comprovantePagDummy));
            when(boletoService.getBoletoReference(anyLong())).thenReturn(existingBoleto);

            comprovantePagamentoService.updateComprovantePagamento(comprovantePagamentoUpdateDto);

            verify(storageService, times(1)).updateFile(any(),any(), any());
            ArgumentCaptor<ComprovantePagamento> comprovanteCaptor = ArgumentCaptor.forClass(ComprovantePagamento.class);
            verify(comprovantePagamentoRepository).saveAndFlush(comprovanteCaptor.capture());
            ComprovantePagamento capturedComprovantePag = comprovanteCaptor.getValue();

            assertThat(capturedComprovantePag.getPaymentDate()).isEqualTo(comprovantePagamentoUpdateDto.paymentDate());
            assertThat(capturedComprovantePag.getValue()).isEqualTo(comprovantePagamentoUpdateDto.value());
            assertThat(capturedComprovantePag.getFileReference()).isEqualTo("comprovPag01.pdf");
            assertThat(capturedComprovantePag.getBoleto().getId()).isEqualTo(2L);
        }
        @Test
        @DisplayName("Should update Comprovante Pagamento successfully when no new file is provided")
        void updateComprovantePagamento_whenFileIsNull_shouldUpdateWithoutCallingStorageService() {
            Boleto existingBoleto = new Boleto();
            existingBoleto.setId(2L);
            ComprovantePagamento comprovantePagDummy = new ComprovantePagamento(
                    LocalDate.now(),
                    BigDecimal.TEN,
                    "comprovPag01.pdf",
                    existingBoleto
            );
            ComprovantePagamentoUpdateDto updateWithNullFile = new ComprovantePagamentoUpdateDto(
                    LocalDate.now(),
                    BigDecimal.TEN,
                    1L,
                    null
            );
            when(comprovantePagamentoRepository.findByBoletoId(anyLong())).thenReturn(Optional.of(comprovantePagDummy));
            when(boletoService.getBoletoReference(anyLong())).thenReturn(existingBoleto);

            // Act
            comprovantePagamentoService.updateComprovantePagamento(updateWithNullFile);

            verify(storageService, never()).updateFile(any(), any(), any());
            ArgumentCaptor<ComprovantePagamento> comprovanteCaptor = ArgumentCaptor.forClass(ComprovantePagamento.class);
            verify(comprovantePagamentoRepository).saveAndFlush(comprovanteCaptor.capture());
            ComprovantePagamento capturedComprovantePag = comprovanteCaptor.getValue();
            assertThat(capturedComprovantePag.getPaymentDate()).isEqualTo(comprovantePagamentoUpdateDto.paymentDate());
            assertThat(capturedComprovantePag.getValue()).isEqualTo(comprovantePagamentoUpdateDto.value());
            assertThat(capturedComprovantePag.getFileReference()).isEqualTo("comprovPag01.pdf");
            assertThat(capturedComprovantePag.getBoleto().getId()).isEqualTo(2L);
        }
        @Test
        @DisplayName("Should call file exchange for given Comprovante Pagamento.")
        void updateComprovantePagamento_WhenNewFileIsProvided_ShouldCallUpdateFile() {
            Boleto existingBoleto = new Boleto();
            existingBoleto.setId(2L);
            ComprovantePagamento comprovantePagDummy = new ComprovantePagamento(
                    LocalDate.now(),
                    BigDecimal.TEN,
                    "comprovPag01.pdf",
                    existingBoleto
            );

            when(comprovantePagamentoRepository.findByBoletoId(anyLong())).thenReturn(Optional.of(comprovantePagDummy));
            when(boletoService.getBoletoReference(anyLong())).thenReturn(existingBoleto);

            // Act
            comprovantePagamentoService.updateComprovantePagamento(comprovantePagamentoUpdateDto);

            verify(storageService, times(1)).updateFile(any(), any(), any());
            ArgumentCaptor<ComprovantePagamento> comprovanteCaptor = ArgumentCaptor.forClass(ComprovantePagamento.class);
            verify(comprovantePagamentoRepository).saveAndFlush(comprovanteCaptor.capture());
            ComprovantePagamento capturedComprovantePag = comprovanteCaptor.getValue();
            assertThat(capturedComprovantePag.getPaymentDate()).isEqualTo(comprovantePagamentoUpdateDto.paymentDate());
            assertThat(capturedComprovantePag.getValue()).isEqualTo(comprovantePagamentoUpdateDto.value());
            assertThat(capturedComprovantePag.getFileReference()).isEqualTo("comprovPag01.pdf");
            assertThat(capturedComprovantePag.getBoleto().getId()).isEqualTo(2L);
        }
        @Test
        @DisplayName("Should throw ComprovanteNotFoundException.")
        void updateComprovantePagamento_WhenComprovantePagamentoNotFound_ShouldThrowComprovantePagamentoNotFoundException() {
            // Arrange
            when(comprovantePagamentoRepository.findByBoletoId(any())).thenThrow(new ComprovantePagamentoNotFoundException(""));

            assertThrows(ComprovantePagamentoNotFoundException.class, () -> {
                comprovantePagamentoService.updateComprovantePagamento(comprovantePagamentoUpdateDto);
            });
            verify(comprovantePagamentoRepository, never()).saveAndFlush(any());
            verify(storageService, never()).updateFile(any(), any(), any());
        }
    }
}
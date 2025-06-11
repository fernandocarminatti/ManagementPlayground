package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.ComprovantePagamentoRegisterDto;
import com.edu.ManagementPlayground.Dto.ComprovantePagamentoResponseDto;
import com.edu.ManagementPlayground.Entity.Boleto;
import com.edu.ManagementPlayground.Entity.ComprovantePagamento;
import com.edu.ManagementPlayground.Enum.StorageContext;
import com.edu.ManagementPlayground.Exception.ComprovantePagamentoNotFoundException;
import com.edu.ManagementPlayground.Repository.ComprovantePagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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


}
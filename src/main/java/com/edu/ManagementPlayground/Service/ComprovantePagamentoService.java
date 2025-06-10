package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.ComprovantePagamentoRegisterDto;
import com.edu.ManagementPlayground.Dto.ComprovantePagamentoResponseDto;
import com.edu.ManagementPlayground.Dto.ComprovantePagamentoUpdateDto;
import com.edu.ManagementPlayground.Entity.Boleto;
import com.edu.ManagementPlayground.Entity.ComprovantePagamento;
import com.edu.ManagementPlayground.Enum.StorageContext;
import com.edu.ManagementPlayground.Repository.ComprovantePagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Set;

@Service
public class ComprovantePagamentoService {

    private final ComprovantePagamentoRepository comprovantePagamentoRepository;
    private final BoletoService boletoService;
    private final StorageService storageService;

    ComprovantePagamentoService(ComprovantePagamentoRepository comprovantePagamentoRepository, BoletoService boletoService, StorageService storageService){
        this.comprovantePagamentoRepository = comprovantePagamentoRepository;
        this.boletoService = boletoService;
        this.storageService = storageService;
    }

    public Set<ComprovantePagamentoResponseDto> getAllComprovante() { return comprovantePagamentoRepository.findAllProjection(); }

    public Resource getComprovantePagamentoFile(String fileReference){
        return storageService.loadAsResource(fileReference, StorageContext.COMPROVANTEPAGAMENTO);
    }

    @Transactional
    public boolean registerComprovantePagamento(ComprovantePagamentoRegisterDto comprovantePagamentoRegisterDto){
        if(comprovantePagamentoRepository.existsByBoletoId(comprovantePagamentoRegisterDto.boletoId())){
            return false;
        }
        Boleto boletoReference = boletoService.getBoletoReference(comprovantePagamentoRegisterDto.boletoId());
        String savedFilePath = storageService.storeFile(comprovantePagamentoRegisterDto.comprovanteFile(),  StorageContext.COMPROVANTEPAGAMENTO);
        ComprovantePagamento comprovante = new ComprovantePagamento(
                comprovantePagamentoRegisterDto.paymentDate(),
                comprovantePagamentoRegisterDto.value(),
                savedFilePath,
                boletoReference // PROXY REFERENCE
        );
        comprovantePagamentoRepository.save(comprovante);
        return true;
    }

    @Transactional
    public void updateComprovantePagamento(ComprovantePagamentoUpdateDto comprovantePagamentoUpdateDto){
        ComprovantePagamento comprovantePagamento = comprovantePagamentoRepository.findByBoletoId(comprovantePagamentoUpdateDto.boletoId()).
                orElseThrow(EntityNotFoundException::new);
        Boleto boletoReference = boletoService.getBoletoReference(comprovantePagamentoUpdateDto.boletoId());
        if(comprovantePagamentoUpdateDto.comprovanteFile() != null){
            storageService.updateFile(comprovantePagamentoUpdateDto.comprovanteFile(), Path.of(comprovantePagamento.getFileReference()), StorageContext.COMPROVANTEPAGAMENTO);
        }
        comprovantePagamento.setPaymentDate(comprovantePagamentoUpdateDto.paymentDate());
        comprovantePagamento.setValue(comprovantePagamentoUpdateDto.value());
        comprovantePagamento.setBoleto(boletoReference);

        comprovantePagamentoRepository.saveAndFlush(comprovantePagamento);
    }
}
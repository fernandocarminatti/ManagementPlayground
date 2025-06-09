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
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Set;

@Service
public class BoletoService {
    private final NotaFiscalRepository notaFiscalRepository;
    private final BoletoRepository boletoRepository;
    private final StorageService storageService;

    BoletoService(NotaFiscalRepository notaFiscalRepository, BoletoRepository boletoRepository, StorageService storageService){
        this.notaFiscalRepository = notaFiscalRepository;
        this.boletoRepository = boletoRepository;
        this.storageService = storageService;
    }

    public Set<BoletoResponseDto> getAllBoletos() { return boletoRepository.findAllProjection(); }

    public Resource getBoletoFile(String fileReference){
        return storageService.loadAsResource(fileReference, StorageContext.BOLETO);
    }

    @Transactional
    public boolean registerBoleto(BoletoRegisterDto boletoRegisterDto){
        if(boletoRepository.existsByTypeableLine(boletoRegisterDto.typeableLine())){
            return false;
        }
        NotaFiscal notaFiscalReference = notaFiscalRepository.getReferenceByNumberIdentifier(boletoRegisterDto.notaFiscalNumberIdentifier());
        String savedFilePath = storageService.storeFile(boletoRegisterDto.boletoFile(), StorageContext.BOLETO);
        Boleto boleto = new Boleto(
                boletoRegisterDto.typeableLine(),
                boletoRegisterDto.dueDate(),
                boletoRegisterDto.value(),
                boletoRegisterDto.paymentStatus(),
                savedFilePath,
                notaFiscalReference // PROXY reference
        );
        boletoRepository.save(boleto);
        return true;
    }

    @Transactional
    public void updateBoleto(BoletoUpdateDto boletoUpdateDto){
        Boleto boleto = boletoRepository.findByTypeableLine(boletoUpdateDto.typeableLine()).
                orElseThrow(EntityNotFoundException::new);
        NotaFiscal notaFiscalReference = notaFiscalRepository.getReferenceByNumberIdentifier(boletoUpdateDto.notaFiscalNumberIdentifier());
        if(boletoUpdateDto.boletoFile() != null){
            storageService.updateFile(boletoUpdateDto.boletoFile(), Path.of(boleto.getFileReference()), StorageContext.BOLETO);
        }
        boleto.setTypeableLine(boletoUpdateDto.typeableLine());
        boleto.setDueDate(boletoUpdateDto.dueDate());
        boleto.setValue(boletoUpdateDto.value());
        boleto.setPaymentStatus(boletoUpdateDto.paymentStatus());
        boleto.setNotaFiscal(notaFiscalReference);

        boletoRepository.saveAndFlush(boleto);
    }
}
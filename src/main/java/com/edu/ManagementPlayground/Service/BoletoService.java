package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.BoletoRegisterDto;
import com.edu.ManagementPlayground.Dto.BoletoResponseDto;
import com.edu.ManagementPlayground.Dto.BoletoUpdateDto;
import com.edu.ManagementPlayground.Entity.Boleto;
import com.edu.ManagementPlayground.Entity.NotaFiscal;
import com.edu.ManagementPlayground.Enum.StorageContext;
import com.edu.ManagementPlayground.Repository.BoletoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Set;

@Service
public class BoletoService {
    private final BoletoRepository boletoRepository;
    private final NotaFiscalService notaFiscalService;
    private final StorageService storageService;

    BoletoService(BoletoRepository boletoRepository, NotaFiscalService notaFiscalService, StorageService storageService){
        this.boletoRepository = boletoRepository;
        this.notaFiscalService = notaFiscalService;
        this.storageService = storageService;
    }

    public Set<BoletoResponseDto> getAllBoletos() { return boletoRepository.findAllProjection(); }

    public Resource getBoletoFile(String fileReference){
        return storageService.loadAsResource(fileReference, StorageContext.BOLETO);
    }

    public Boleto getBoletoReference(long id){
        return boletoRepository.getReferenceById(id);
    }

    @Transactional
    public boolean registerBoleto(BoletoRegisterDto boletoRegisterDto){
        if(boletoRepository.existsByTypeableLine(boletoRegisterDto.typeableLine())){
            return false;
        }
        NotaFiscal notaFiscalReference = notaFiscalService.getNotaFiscalReference(boletoRegisterDto.notaFiscalId());
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
        NotaFiscal notaFiscalReference = notaFiscalService.getNotaFiscalReference(boletoUpdateDto.notaFiscalId());
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
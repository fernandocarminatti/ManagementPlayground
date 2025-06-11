package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.BoletoRegisterDto;
import com.edu.ManagementPlayground.Dto.BoletoResponseDto;
import com.edu.ManagementPlayground.Dto.BoletoUpdateDto;
import com.edu.ManagementPlayground.Entity.Boleto;
import com.edu.ManagementPlayground.Entity.NotaFiscal;
import com.edu.ManagementPlayground.Enum.StorageContext;
import com.edu.ManagementPlayground.Exception.BoletoNotFoundException;
import com.edu.ManagementPlayground.Repository.BoletoRepository;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
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

    public BoletoResponseDto getBoleto(long id){
        Boleto boleto = boletoRepository.findById(id).orElseThrow(() -> new BoletoNotFoundException("Could not find any Boleto with given attributes"));
        return new BoletoResponseDto(
                boleto.getId(),
                boleto.getTypeableLine(),
                boleto.getDueDate(),
                boleto.getValue(),
                boleto.getPaymentStatus(),
                boleto.getFileReference()
        );
    }

    @Transactional
    public boolean registerBoleto(BoletoRegisterDto boletoRegisterDto){
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
        try{
            boletoRepository.save(boleto);
            return true;
        } catch (DataIntegrityViolationException e){
            storageService.deleteFile(savedFilePath, StorageContext.BOLETO);
            throw new BoletoNotFoundException("A Boleto with provided attributes already exists.");
        }
    }

    @Transactional
    public void updateBoleto(BoletoUpdateDto boletoUpdateDto){
        Boleto boleto = boletoRepository.findByTypeableLine(boletoUpdateDto.typeableLine()).
                orElseThrow(() -> new BoletoNotFoundException("Could not find any Boleto with provided attributes."));
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
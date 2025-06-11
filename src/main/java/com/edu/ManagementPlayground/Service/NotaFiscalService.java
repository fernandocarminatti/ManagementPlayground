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
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Set;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final SupplierService supplierService;
    private final StorageService storageService;

    public NotaFiscalService(NotaFiscalRepository notaFiscalRepository, SupplierService supplierService, StorageService storageService){
        this.notaFiscalRepository = notaFiscalRepository;
        this.supplierService = supplierService;
        this.storageService = storageService;
    }

    public Set<NotaFiscalResponseDto> getAllNotaFiscal(){
        return notaFiscalRepository.findAllWithSupplierId();
    }


    public NotaFiscalResponseDto getNotaFiscal(long id){
        NotaFiscal notafiscal = notaFiscalRepository.findById(id).orElseThrow(() -> new NotaFiscalNotFoundException("Could not find any Nota Fiscal with provided attributes."));
        return new NotaFiscalResponseDto(
                notafiscal.getId(),
                notafiscal.getNumberIdentifier(),
                notafiscal.getIssueDate(),
                notafiscal.getTotalValue(),
                notafiscal.getFileReference(),
                notafiscal.getSupplier().getId()
        );
    }

    public Resource getNotaFiscalFile(String fileReference){
        return storageService.loadAsResource(fileReference, StorageContext.NOTAFISCAL);
    }

    public NotaFiscal getNotaFiscalReference(long id){
        return notaFiscalRepository.getReferenceById(id);
    }

    @Transactional
    public String registerNotaFiscal(NotaFiscalRegisterDto notaFiscalRegisterDto){
        Supplier supplierReference = supplierService.getSupplierReference(notaFiscalRegisterDto.supplierId());
        String savedFilePath = storageService.storeFile(notaFiscalRegisterDto.notaFiscalFile(), StorageContext.NOTAFISCAL);
        NotaFiscal notaFiscal = new NotaFiscal(
                notaFiscalRegisterDto.numberIdentifier(),
                notaFiscalRegisterDto.issueDate(),
                notaFiscalRegisterDto.totalValue(),
                savedFilePath,
                supplierReference // PROXY reference
        );
        try {
            notaFiscalRepository.save(notaFiscal);
            return savedFilePath;
        } catch (DataIntegrityViolationException e){
            storageService.deleteFile(savedFilePath, StorageContext.NOTAFISCAL);
            throw new NotaFiscalAlreadyExistsException("A Nota Fiscal with provided attributes already exists.");
        }
    }

    @Transactional
    public void updateNotaFiscal(NotaFiscalUpdateDto notaFiscalUpdateDto){
        NotaFiscal notaFiscal = notaFiscalRepository.findByNumberIdentifier(notaFiscalUpdateDto.numberIdentifier()).
                orElseThrow(() -> new NotaFiscalNotFoundException("Could not find any Nota Fiscal with provided attributes."));
        Supplier supplierReference = supplierService.getSupplierReference(notaFiscalUpdateDto.supplierId());
        if(notaFiscalUpdateDto.objectFile() != null){
            storageService.updateFile(notaFiscalUpdateDto.objectFile(), Path.of(notaFiscal.getFileReference()), StorageContext.NOTAFISCAL);
        }
        notaFiscal.setNumberIdentifier(notaFiscalUpdateDto.numberIdentifier());
        notaFiscal.setIssueDate(notaFiscalUpdateDto.issueDate());
        notaFiscal.setTotalValue(notaFiscalUpdateDto.totalValue());
        notaFiscal.setSupplier(supplierReference);

        notaFiscalRepository.saveAndFlush(notaFiscal);
    }
}
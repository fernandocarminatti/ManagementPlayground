package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.NotaFiscalRegisterDto;
import com.edu.ManagementPlayground.Dto.NotaFiscalResponseDto;
import com.edu.ManagementPlayground.Dto.NotaFiscalUpdateDto;
import com.edu.ManagementPlayground.Entity.NotaFiscal;
import com.edu.ManagementPlayground.Entity.Supplier;
import com.edu.ManagementPlayground.Enum.StorageContext;
import com.edu.ManagementPlayground.Repository.NotaFiscalRepository;
import com.edu.ManagementPlayground.Repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final SupplierRepository supplierRepository;
    private final StorageService storageService;

    public NotaFiscalService(NotaFiscalRepository notaFiscalRepository, SupplierRepository supplierRepository, StorageService storageService){
        this.notaFiscalRepository = notaFiscalRepository;
        this.supplierRepository = supplierRepository;
        this.storageService = storageService;
    }

    public List<NotaFiscalResponseDto> getAllNotaFiscal(){
        return notaFiscalRepository.findAllWithSupplierId();
    }

    public Resource getNotaFiscalFile(String fileReference){
        return storageService.loadAsResource(fileReference, StorageContext.NOTAFISCAL);
    }

    @Transactional
    public boolean registerNotaFiscal(NotaFiscalRegisterDto notaFiscalRegisterDto){
        if(notaFiscalRepository.existsByNumberIdentifier(notaFiscalRegisterDto.numberIdentifier())){
            return false;
        }
        Supplier supplierReference = supplierRepository.getReferenceById(notaFiscalRegisterDto.supplierId());
        String savedFilePath = storageService.storeFile(notaFiscalRegisterDto.objectFile(), StorageContext.NOTAFISCAL);
        NotaFiscal notaFiscal = new NotaFiscal(
                notaFiscalRegisterDto.numberIdentifier(),
                notaFiscalRegisterDto.issueDate(),
                notaFiscalRegisterDto.totalValue(),
                savedFilePath,
                supplierReference // PROXY reference
        );
        notaFiscalRepository.save(notaFiscal);
        return true;
    }

    @Transactional
    public void updateNotaFiscal(NotaFiscalUpdateDto notaFiscalUpdateDto){
        NotaFiscal notaFiscal = notaFiscalRepository.findByNumberIdentifier(notaFiscalUpdateDto.numberIdentifier()).
                orElseThrow(() -> new EntityNotFoundException("Nota Fiscal not found with: " + notaFiscalUpdateDto.numberIdentifier()));
        Supplier supplierReference = getSupplierReference(notaFiscalUpdateDto.supplierId());
        if(notaFiscalUpdateDto.objectFile() != null){
            storageService.updateFile(notaFiscalUpdateDto.objectFile(), Path.of(notaFiscal.getFileReference()), StorageContext.NOTAFISCAL);
        }
        notaFiscal.setNumberIdentifier(notaFiscalUpdateDto.numberIdentifier());
        notaFiscal.setIssueDate(notaFiscalUpdateDto.issueDate());
        notaFiscal.setTotalValue(notaFiscalUpdateDto.totalValue());
        notaFiscal.setSupplier(supplierReference);

        notaFiscalRepository.saveAndFlush(notaFiscal);
    }


    private Supplier getSupplierReference(long refIdentifier){
        return supplierRepository.getReferenceById(refIdentifier);
    }
}
package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.NotaFiscalRegisterDto;
import com.edu.ManagementPlayground.Dto.NotaFiscalResponseDto;
import com.edu.ManagementPlayground.Entity.NotaFiscal;
import com.edu.ManagementPlayground.Entity.Supplier;
import com.edu.ManagementPlayground.Repository.NotaFiscalRepository;
import com.edu.ManagementPlayground.Repository.SupplierRepository;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        boolean existsCheck = notaFiscalRepository.existsByfileReference(fileReference);
        if(!existsCheck){
            return null;
        }

        Resource resource;
        try {
            resource = storageService.loadAsResource(fileReference);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: " + fileReference, e);
        }
        return resource;
    }

    public boolean registerNotaFiscal(NotaFiscalRegisterDto notaFiscalRegisterDto){
        if(notaFiscalRepository.existsByNumberIdentifier(notaFiscalRegisterDto.numberIdentifier())){
            return false;
        }
        Supplier supplierReference = supplierRepository.getReferenceById(notaFiscalRegisterDto.supplierId());
        String savedFilePath = storageService.storeFile(notaFiscalRegisterDto.objectFile(), notaFiscalRegisterDto.supplierId());
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

    /*public boolean updateNotaFiscal(NotaFiscalRegisterDto notaFiscalRegisterDto){
        Optional<NotaFiscal> notaFiscalEntity = notaFiscalRepository.findByNumberIdentifier(notaFiscalRegisterDto.numberIdentifier());
        if(notaFiscalEntity.isEmpty()){
            return false;
        }
        Supplier supplierReference = getSupplierReference(notaFiscalRegisterDto.supplierId());
        notaFiscalEntity.get().setNumberIdentifier(notaFiscalRegisterDto.numberIdentifier());
        notaFiscalEntity.get().setIssueDate(notaFiscalRegisterDto.issueDate());
        notaFiscalEntity.get().setTotalValue(notaFiscalRegisterDto.totalValue());
        notaFiscalEntity.get().setFileReference(notaFiscalRegisterDto.fileReference());
        notaFiscalEntity.get().setSupplier(supplierReference);

        notaFiscalRepository.save(notaFiscalEntity.get());
        return true;
    }*/


    private Supplier getSupplierReference(long refIdentifier){
        return supplierRepository.getReferenceById(refIdentifier);
    }
}
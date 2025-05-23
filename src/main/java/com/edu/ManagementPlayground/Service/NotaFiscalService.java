package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.NotaFiscalRegisterDto;
import com.edu.ManagementPlayground.Dto.NotaFiscalResponseDto;
import com.edu.ManagementPlayground.Entity.NotaFiscal;
import com.edu.ManagementPlayground.Entity.Supplier;
import com.edu.ManagementPlayground.Repository.NotaFiscalRepository;
import com.edu.ManagementPlayground.Repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final SupplierRepository supplierRepository;

    public NotaFiscalService(NotaFiscalRepository notaFiscalRepository, SupplierRepository supplierRepository){
        this.notaFiscalRepository = notaFiscalRepository;
        this.supplierRepository = supplierRepository;
    }

    public List<NotaFiscalResponseDto> getAllNotaFiscal(){
        return notaFiscalRepository.findAllWithSupplierId();
    }

    public boolean registerNotaFiscal(NotaFiscalRegisterDto notaFiscalRegisterDto){
        if(notaFiscalRepository.existsByNumberIdentifier(notaFiscalRegisterDto.numberIdentifier())){
            return false;
        }
        Supplier supplierReference = supplierRepository.getReferenceById(notaFiscalRegisterDto.supplierId());
        NotaFiscal notaFiscal = new NotaFiscal(
                notaFiscalRegisterDto.numberIdentifier(),
                notaFiscalRegisterDto.issueDate(),
                notaFiscalRegisterDto.totalValue(),
                notaFiscalRegisterDto.fileReference(),
                supplierReference // PROXY reference
        );
        notaFiscalRepository.save(notaFiscal);
        return true;
    }

    public boolean updateNotaFiscal(NotaFiscalRegisterDto notaFiscalRegisterDto){
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
    }


    private Supplier getSupplierReference(long refIdentifier){
        return supplierRepository.getReferenceById(refIdentifier);
    }
}
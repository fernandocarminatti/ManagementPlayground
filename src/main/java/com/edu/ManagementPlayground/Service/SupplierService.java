package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.SupplierRegisterDto;
import com.edu.ManagementPlayground.Dto.SupplierResponseDto;
import com.edu.ManagementPlayground.Entity.Supplier;
import com.edu.ManagementPlayground.Exception.SupplierAlreadyExistsException;
import com.edu.ManagementPlayground.Exception.SupplierNotFoundException;
import com.edu.ManagementPlayground.Repository.SupplierRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;

    SupplierService(SupplierRepository supplierRepository){
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers(){
        return supplierRepository.findAll();
    }

    public SupplierResponseDto getSupplier(long id){
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new SupplierNotFoundException("Could not find Supplier with given attributes"));
        return new SupplierResponseDto(
                supplier.getId(),
                supplier.getName(),
                supplier.getCnpj(),
                supplier.getEmail(),
                supplier.getPhoneNumber()
        );
    }

    public Supplier getSupplierReference(long id){
        return supplierRepository.getReferenceById(id);
    }

    public boolean registerSupplier(SupplierRegisterDto supplierRegisterDto){
        Supplier supplier = new Supplier(supplierRegisterDto.name(), supplierRegisterDto.cnpj(), supplierRegisterDto.email(), supplierRegisterDto.phoneNumber());
        try{
            supplierRepository.save(supplier);
            return true;
        } catch (DataIntegrityViolationException e){
            throw new SupplierAlreadyExistsException("A Supplier with provided attributes already exists.");
        }
    }

    public boolean updateSupplier(SupplierRegisterDto supplierRegisterDto){
        Optional<Supplier> supplierEntity = Optional.of(supplierRepository.findByCnpj(supplierRegisterDto.cnpj()).orElseThrow( () -> new SupplierNotFoundException("Could not find any Supplier with provided attributes.")));

        supplierEntity.get().setName(supplierRegisterDto.name());
        supplierEntity.get().setCnpj(supplierRegisterDto.cnpj());
        supplierEntity.get().setEmail(supplierRegisterDto.email());
        supplierEntity.get().setPhoneNumber(supplierRegisterDto.phoneNumber());

        supplierRepository.save(supplierEntity.get());
        return true;
    }
}
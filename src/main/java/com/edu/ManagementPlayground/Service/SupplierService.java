package com.edu.ManagementPlayground.Service;

import com.edu.ManagementPlayground.Dto.SupplierRegisterDto;
import com.edu.ManagementPlayground.Entity.Supplier;
import com.edu.ManagementPlayground.Repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {
    SupplierRepository supplierRepository;

    SupplierService(SupplierRepository supplierRepository){
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers(){
        return supplierRepository.findAll();
    }

    public boolean registerSupplier(SupplierRegisterDto supplierRegisterDto){
        if(supplierRepository.existsByCnpj(supplierRegisterDto.cnpj())){
            return false;
        }
        Supplier supplier = new Supplier(supplierRegisterDto.name(), supplierRegisterDto.cnpj(), supplierRegisterDto.email(), supplierRegisterDto.phoneNumber());
        supplierRepository.save(supplier);
        return true;
    }

    public boolean updateSupplier(SupplierRegisterDto supplierRegisterDto){
        Optional<Supplier> supplierEntity = supplierRepository.findByCnpj(supplierRegisterDto.cnpj());
        if(supplierEntity.isEmpty()){
            return false;
        }
        supplierEntity.get().setName(supplierRegisterDto.name());
        supplierEntity.get().setCnpj(supplierRegisterDto.cnpj());
        supplierEntity.get().setEmail(supplierRegisterDto.email());
        supplierEntity.get().setPhoneNumber(supplierRegisterDto.phoneNumber());

        supplierRepository.save(supplierEntity.get());
        return true;
    }
}
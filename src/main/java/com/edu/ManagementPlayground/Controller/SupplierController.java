package com.edu.ManagementPlayground.Controller;

import com.edu.ManagementPlayground.Dto.SupplierRegisterDto;
import com.edu.ManagementPlayground.Dto.SupplierResponseDto;
import com.edu.ManagementPlayground.Service.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/suppliers")
public class SupplierController {

    SupplierService supplierService;

    SupplierController(SupplierService supplierService){
        this.supplierService = supplierService;
    }

    @GetMapping()
    public ResponseEntity<List<SupplierResponseDto>> getAllSuppliers(){
        List<SupplierResponseDto> allSuppliers = supplierService.getAllSuppliers().stream().map(SupplierResponseDto::fromEntity).toList();
        return ResponseEntity.status(200).body(allSuppliers);
    }

    @PostMapping("/register")
    public ResponseEntity<?> createNewSupplier(@RequestBody SupplierRegisterDto supplierRegisterDto){
        boolean registrationOperation = supplierService.registerSupplier(supplierRegisterDto);
        if(registrationOperation){
            return ResponseEntity.status(201).build();
        }
        return ResponseEntity.status(409).build();
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateSupplier(@RequestBody SupplierRegisterDto supplierRegisterDto){
        boolean updateOperation = supplierService.updateSupplier(supplierRegisterDto);
        return updateOperation ? ResponseEntity.status(200).build() : ResponseEntity.status(400).build();
    }
}
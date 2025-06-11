package com.edu.ManagementPlayground.Controller;

import com.edu.ManagementPlayground.Dto.SupplierRegisterDto;
import com.edu.ManagementPlayground.Dto.SupplierResponseDto;
import com.edu.ManagementPlayground.Entity.Supplier;
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
    public ResponseEntity<List<Supplier>> getAllSuppliers(){
        List<Supplier> allSuppliers = supplierService.getAllSuppliers();
        return ResponseEntity.status(200).body(allSuppliers);
    }

    @GetMapping("/{supplierId}")
    public ResponseEntity<SupplierResponseDto> getSupplier(@PathVariable long supplierId){
        SupplierResponseDto responseDto = supplierService.getSupplier(supplierId);
        return ResponseEntity.status(200).body(responseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> createNewSupplier(@RequestBody SupplierRegisterDto supplierRegisterDto){
        boolean registrationOperation = supplierService.registerSupplier(supplierRegisterDto);
        if(registrationOperation){
            return ResponseEntity.status(201).build();
        }
        return ResponseEntity.status(409).build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateSupplier(@RequestBody SupplierRegisterDto supplierRegisterDto){
        boolean updateOperation = supplierService.updateSupplier(supplierRegisterDto);
        return updateOperation ? ResponseEntity.status(200).build() : ResponseEntity.status(400).build();
    }
}
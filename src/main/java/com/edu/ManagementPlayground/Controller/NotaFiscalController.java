package com.edu.ManagementPlayground.Controller;

import com.edu.ManagementPlayground.Dto.NotaFiscalRegisterDto;
import com.edu.ManagementPlayground.Dto.NotaFiscalResponseDto;
import com.edu.ManagementPlayground.Service.NotaFiscalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/NotasFiscais")
public class NotaFiscalController {

    NotaFiscalService notaFiscalService;

    NotaFiscalController(NotaFiscalService notaFiscalService){
        this.notaFiscalService = notaFiscalService;
    }


    @GetMapping()
    public ResponseEntity<List<NotaFiscalResponseDto>> getAllSuppliers(){
        List<NotaFiscalResponseDto> allSuppliers = notaFiscalService.getAllNotaFiscal();
        return ResponseEntity.status(200).body(allSuppliers);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> createNewSupplier(@RequestBody NotaFiscalRegisterDto notaFiscalRegisterDto){
        boolean registrationOperation = notaFiscalService.registerNotaFiscal(notaFiscalRegisterDto);
        if(registrationOperation){
            return ResponseEntity.status(201).build();
        }
        return ResponseEntity.status(409).build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateSupplier(@RequestBody NotaFiscalRegisterDto notaFiscalRegisterDto){
        boolean updateOperation = notaFiscalService.updateNotaFiscal(notaFiscalRegisterDto);
        return updateOperation ? ResponseEntity.status(200).build() : ResponseEntity.status(400).build();
    }


}
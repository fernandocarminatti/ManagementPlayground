package com.edu.ManagementPlayground.Controller;

import com.edu.ManagementPlayground.Dto.NotaFiscalRegisterDto;
import com.edu.ManagementPlayground.Dto.NotaFiscalResponseDto;
import com.edu.ManagementPlayground.Service.NotaFiscalService;
import com.edu.ManagementPlayground.Service.StorageService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/NotasFiscais")
public class NotaFiscalController {

    NotaFiscalService notaFiscalService;
    StorageService storageService;

    NotaFiscalController(NotaFiscalService notaFiscalService, StorageService storageService){
        this.notaFiscalService = notaFiscalService;
        this.storageService = storageService;
    }

    @GetMapping()
    public ResponseEntity<List<NotaFiscalResponseDto>> getAllSuppliers(){
        List<NotaFiscalResponseDto> allSuppliers = notaFiscalService.getAllNotaFiscal();
        return ResponseEntity.status(200).body(allSuppliers);
    }

    @GetMapping("/uploads/{fileReference:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileReference) {
        Resource fileToServe = notaFiscalService.getNotaFiscalFile(fileReference);
        if(fileToServe == null){
            return ResponseEntity.status(409).build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(MediaType.APPLICATION_PDF_VALUE))
                .body(fileToServe);
    }

    @PostMapping(value = "/register", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Void> createNewNotaFiscal(@Valid @ModelAttribute NotaFiscalRegisterDto notaFiscalRegisterDto){
        boolean registrationOperation = notaFiscalService.registerNotaFiscal(notaFiscalRegisterDto);
        if(registrationOperation){
            return ResponseEntity.status(201).build();
        }
        return ResponseEntity.status(409).build();
    }

    /*@PutMapping("/update")
    public ResponseEntity<Void> updateSupplier(@RequestBody NotaFiscalRegisterDto notaFiscalRegisterDto){
        boolean updateOperation = notaFiscalService.updateNotaFiscal(notaFiscalRegisterDto);
        return updateOperation ? ResponseEntity.status(200).build() : ResponseEntity.status(400).build();
    }*/


}
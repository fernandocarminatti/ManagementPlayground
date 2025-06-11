package com.edu.ManagementPlayground.Controller;

import com.edu.ManagementPlayground.Dto.NotaFiscalRegisterDto;
import com.edu.ManagementPlayground.Dto.NotaFiscalResponseDto;
import com.edu.ManagementPlayground.Dto.NotaFiscalUpdateDto;
import com.edu.ManagementPlayground.Service.NotaFiscalService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("v1/notasfiscais")
public class NotaFiscalController {

    NotaFiscalService notaFiscalService;

    NotaFiscalController(NotaFiscalService notaFiscalService){
        this.notaFiscalService = notaFiscalService;
    }

    @GetMapping()
    public ResponseEntity<Set<NotaFiscalResponseDto>> getAllSuppliers(){
        Set<NotaFiscalResponseDto> allSuppliers = notaFiscalService.getAllNotaFiscal();
        return ResponseEntity.status(200).body(allSuppliers);
    }

    @GetMapping("/{notaFiscalId}")
    public ResponseEntity<NotaFiscalResponseDto> getSupplier(@PathVariable long notaFiscalId){
        NotaFiscalResponseDto responseDto = notaFiscalService.getNotaFiscal(notaFiscalId);
        return ResponseEntity.status(200).body(responseDto);
    }

    @GetMapping("/uploads/{fileReference:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileReference) {
        Resource fileToServe = notaFiscalService.getNotaFiscalFile(fileReference);
        if(fileToServe.exists() && fileToServe.isReadable()){
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(MediaType.APPLICATION_PDF_VALUE))
                    .body(fileToServe);
        }
        return ResponseEntity.status(404).build();
    }

    @PostMapping(value = "/register", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Void> createNotaFiscal(@Valid @ModelAttribute NotaFiscalRegisterDto notaFiscalRegisterDto){
        boolean registrationOperation = notaFiscalService.registerNotaFiscal(notaFiscalRegisterDto);
        if(registrationOperation){
            return ResponseEntity.status(201).build();
        }
        return ResponseEntity.status(409).build();
    }

    @PutMapping(value = "/update", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Void> updateNotaFiscal(@Valid @ModelAttribute NotaFiscalUpdateDto notaFiscalUpdateDto){
        notaFiscalService.updateNotaFiscal(notaFiscalUpdateDto);
        return ResponseEntity.noContent().build();
    }


}
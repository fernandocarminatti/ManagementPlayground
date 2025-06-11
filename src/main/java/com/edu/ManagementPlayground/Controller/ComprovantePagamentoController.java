package com.edu.ManagementPlayground.Controller;

import com.edu.ManagementPlayground.Dto.ComprovantePagamentoRegisterDto;
import com.edu.ManagementPlayground.Dto.ComprovantePagamentoResponseDto;
import com.edu.ManagementPlayground.Dto.ComprovantePagamentoUpdateDto;
import com.edu.ManagementPlayground.Service.ComprovantePagamentoService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("v1/comprovantes")
public class ComprovantePagamentoController {

    ComprovantePagamentoService comprovantePagamentoService;

    ComprovantePagamentoController(ComprovantePagamentoService comprovantePagamentoService){
        this.comprovantePagamentoService = comprovantePagamentoService;
    }

    @GetMapping()
    public ResponseEntity<Set<ComprovantePagamentoResponseDto>> getAllComprovantes(){
        Set<ComprovantePagamentoResponseDto> allComprovantes = comprovantePagamentoService.getAllComprovante();
        return ResponseEntity.status(200).body(allComprovantes);
    }

    @GetMapping("/{comprovanteId}")
    public ResponseEntity<ComprovantePagamentoResponseDto> getSupplier(@PathVariable long comprovanteId){
        ComprovantePagamentoResponseDto responseDto = comprovantePagamentoService.getComprovante(comprovanteId);
        return ResponseEntity.status(200).body(responseDto);
    }

    @GetMapping("/uploads/{fileReference:.+}")
    public ResponseEntity<Resource> serveComprovanteFile(@PathVariable String fileReference) {
        Resource fileToServe = comprovantePagamentoService.getComprovantePagamentoFile(fileReference);
        if(fileToServe.exists() && fileToServe.isReadable()){
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(MediaType.APPLICATION_PDF_VALUE))
                    .body(fileToServe);
        }
        return ResponseEntity.status(404).build();
    }

    @PostMapping(value = "/register", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Void> createComprovante(@Valid @ModelAttribute ComprovantePagamentoRegisterDto comprovantePagamentoRegisterDto){
        boolean registrationOperation = comprovantePagamentoService.registerComprovantePagamento(comprovantePagamentoRegisterDto);
        if(registrationOperation){
            return ResponseEntity.status(201).build();
        }
        return ResponseEntity.status(409).build();
    }

    @PutMapping(value = "/update", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Void> updateComprovante(@Valid @ModelAttribute ComprovantePagamentoUpdateDto comprovantePagamentoUpdateDto){
        comprovantePagamentoService.updateComprovantePagamento(comprovantePagamentoUpdateDto);
        return ResponseEntity.noContent().build();
    }
}
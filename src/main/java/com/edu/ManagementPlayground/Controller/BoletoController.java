package com.edu.ManagementPlayground.Controller;

import com.edu.ManagementPlayground.Dto.BoletoRegisterDto;
import com.edu.ManagementPlayground.Dto.BoletoResponseDto;
import com.edu.ManagementPlayground.Dto.BoletoUpdateDto;
import com.edu.ManagementPlayground.Service.BoletoService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("v1/boletos")
public class BoletoController {

    private final BoletoService boletoService;

    BoletoController(BoletoService boletoService){
        this.boletoService = boletoService;
    }

    @GetMapping()
    public ResponseEntity<Set<BoletoResponseDto>> getAllBoletos(){
        Set<BoletoResponseDto> allBoletos = boletoService.getAllBoletos();
        return ResponseEntity.ok(allBoletos);
    }

    @GetMapping("/{boletoId}")
    public ResponseEntity<BoletoResponseDto> getSupplier(@PathVariable long boletoId){
        BoletoResponseDto responseDto = boletoService.getBoleto(boletoId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/uploads/{fileReference:.+}")
    public ResponseEntity<Resource> serveBoletoResource(@PathVariable String fileReference) {
        Resource fileToServe = boletoService.getBoletoFile(fileReference);
        if(fileToServe.exists() && fileToServe.isReadable()){
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(MediaType.APPLICATION_PDF_VALUE))
                    .body(fileToServe);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/register", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Void> createBoleto(@Valid @ModelAttribute BoletoRegisterDto boletoRegisterDto){
        String boletoFileReference = boletoService.registerBoleto(boletoRegisterDto);
        return ResponseEntity.created(URI.create("v1/boletos/uploads/" + boletoFileReference)).build();
    }

    @PutMapping(value = "/update", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Void> updateBoleto(@Valid @ModelAttribute BoletoUpdateDto boletoUpdateDto){
        boletoService.updateBoleto(boletoUpdateDto);
        return ResponseEntity.noContent().build();
    }
}
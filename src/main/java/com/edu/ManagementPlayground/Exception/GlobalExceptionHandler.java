package com.edu.ManagementPlayground.Exception;

import jakarta.validation.UnexpectedTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, Object> errorsReturn = new HashMap<>();
        List<String> errorList = new ArrayList<>();
        errorList.add(e.getMessage());
        errorsReturn.put("Errors: ", errorList);
        return ResponseEntity.status(400).body(errorsReturn);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, Object> errorsReturn = new HashMap<>();
        List<String> errorList = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(error -> errorList.add(error.getDefaultMessage()));
        errorsReturn.put("Errors: ", errorList);
        return ResponseEntity.status(400).body(errorsReturn);
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    private ResponseEntity<Map<String, String>> handleUnexpectedTypeException(UnexpectedTypeException e){
        return ResponseEntity.status(500).body(Map.of("Error", "Internal server error."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(400).body(Map.of("Error: ", "Invalid JSON Structure."));
    }

    @ExceptionHandler(StorageException.class)
    private ResponseEntity<Map<String, Object>> handleStorageException(StorageException e) {
        Map<String, Object> errorsReturn = new HashMap<>();
        List<String> errorList = new ArrayList<>();
        errorList.add(e.getMessage());
        errorsReturn.put("Errors: ", errorList);
        return ResponseEntity.status(400).body(errorsReturn);
    }

    @ExceptionHandler(BoletoNotFoundException.class)
    private ResponseEntity<Map<String, Object>> handleBoletoNotFoundException(BoletoNotFoundException e){
        Map<String, Object> errorMessage = new HashMap<>();
        errorMessage.put("timestamp", LocalDateTime.now());
        errorMessage.put("status", HttpStatus.NOT_FOUND.value());
        errorMessage.put("error", "Not Found");
        errorMessage.put("message", e.getMessage());
        return ResponseEntity.status(404).body(errorMessage);
    }

    @ExceptionHandler(BoletoAlreadyExistsException.class)
    private ResponseEntity<Map<String, Object>> handleBoletoAlreadyExistsException(BoletoAlreadyExistsException e){
        Map<String, Object> errorMessage = new HashMap<>();
        errorMessage.put("timestamp", LocalDateTime.now());
        errorMessage.put("status", HttpStatus.CONFLICT.value());
        errorMessage.put("error", "Conflict");
        errorMessage.put("message", e.getMessage());
        return ResponseEntity.status(409).body(errorMessage);
    }

    @ExceptionHandler(NotaFiscalNotFoundException.class)
    private ResponseEntity<Map<String, Object>> handleNotaFiscalNotFoundException(NotaFiscalNotFoundException e){
        Map<String, Object> errorMessage = new HashMap<>();
        errorMessage.put("timestamp", LocalDateTime.now());
        errorMessage.put("status", HttpStatus.NOT_FOUND.value());
        errorMessage.put("error", "Not Found");
        errorMessage.put("message", e.getMessage());
        return ResponseEntity.status(404).body(errorMessage);
    }

    @ExceptionHandler(NotaFiscalAlreadyExistsException.class)
    private ResponseEntity<Map<String, Object>> handleNotaFiscalAlreadyExistsException(NotaFiscalAlreadyExistsException e){
        Map<String, Object> errorMessage = new HashMap<>();
        errorMessage.put("timestamp", LocalDateTime.now());
        errorMessage.put("status", HttpStatus.CONFLICT.value());
        errorMessage.put("error", "Conflict");
        errorMessage.put("message", e.getMessage());
        return ResponseEntity.status(409).body(errorMessage);
    }

    @ExceptionHandler(ComprovantePagamentoNotFoundException.class)
    private ResponseEntity<Map<String, Object>> handleComprovantePagamentoNotFoundException(ComprovantePagamentoNotFoundException e){
        Map<String, Object> errorMessage = new HashMap<>();
        errorMessage.put("timestamp", LocalDateTime.now());
        errorMessage.put("status", HttpStatus.NOT_FOUND.value());
        errorMessage.put("error", "Not Found");
        errorMessage.put("message", e.getMessage());
        return ResponseEntity.status(404).body(errorMessage);
    }


    @ExceptionHandler(ComprovantePagamentoAlreadyExistsException.class)
    private ResponseEntity<Map<String, Object>> handleComprovantePagamentoAlreadyExistsException(ComprovantePagamentoAlreadyExistsException e){
        Map<String, Object> errorMessage = new HashMap<>();
        errorMessage.put("timestamp", LocalDateTime.now());
        errorMessage.put("status", HttpStatus.CONFLICT.value());
        errorMessage.put("error", "Conflict");
        errorMessage.put("message", e.getMessage());
        return ResponseEntity.status(409).body(errorMessage);
    }
}
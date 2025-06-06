package com.edu.ManagementPlayground.Exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.UnexpectedTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
        errorsReturn.put("Reasons: ", errorList);
        return ResponseEntity.status(400).body(errorsReturn);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, Object> errorsReturn = new HashMap<>();
        List<String> errorList = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(error -> errorList.add(error.getDefaultMessage()));
        errorsReturn.put("Reasons: ", errorList);
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
        errorsReturn.put("Reasons: ", errorList);
        return ResponseEntity.status(400).body(errorsReturn);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    private ResponseEntity<Void> handleEntityNotFoundException(EntityNotFoundException e){
        return ResponseEntity.status(404).build();
    }
}
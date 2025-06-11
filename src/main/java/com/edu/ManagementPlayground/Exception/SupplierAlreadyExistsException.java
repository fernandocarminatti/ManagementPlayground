package com.edu.ManagementPlayground.Exception;

import org.springframework.dao.DataIntegrityViolationException;

public class SupplierAlreadyExistsException extends DataIntegrityViolationException {
    public SupplierAlreadyExistsException(String message) { super(message); }
    public SupplierAlreadyExistsException(String message, Throwable throwable) { super(message, throwable); }
}
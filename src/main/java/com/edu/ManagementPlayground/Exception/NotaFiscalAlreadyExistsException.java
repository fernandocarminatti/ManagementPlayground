package com.edu.ManagementPlayground.Exception;

import org.springframework.dao.DataIntegrityViolationException;

public class NotaFiscalAlreadyExistsException extends DataIntegrityViolationException {
    public NotaFiscalAlreadyExistsException(String message) { super(message); }
    public NotaFiscalAlreadyExistsException(String message, Throwable throwable) { super(message, throwable); }
}
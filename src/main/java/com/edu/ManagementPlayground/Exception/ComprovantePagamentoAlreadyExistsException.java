package com.edu.ManagementPlayground.Exception;

import org.springframework.dao.DataIntegrityViolationException;

public class ComprovantePagamentoAlreadyExistsException extends DataIntegrityViolationException {
    public ComprovantePagamentoAlreadyExistsException(String message) { super(message); }
    public ComprovantePagamentoAlreadyExistsException(String message, Throwable throwable) { super(message, throwable); }
}
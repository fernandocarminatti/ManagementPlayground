package com.edu.ManagementPlayground.Exception;

import org.springframework.dao.DataIntegrityViolationException;

public class BoletoAlreadyExistsException extends DataIntegrityViolationException {
    public BoletoAlreadyExistsException(String message) { super(message); }
    public BoletoAlreadyExistsException(String message, Throwable throwable) { super(message, throwable); }
}
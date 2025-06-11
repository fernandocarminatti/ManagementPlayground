package com.edu.ManagementPlayground.Exception;

import jakarta.persistence.EntityNotFoundException;

public class NotaFiscalNotFoundException extends EntityNotFoundException {
    public NotaFiscalNotFoundException(String message) { super(message); }
    public NotaFiscalNotFoundException(String message, Exception exception) { super(message, exception); }
}
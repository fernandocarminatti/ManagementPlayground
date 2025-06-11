package com.edu.ManagementPlayground.Exception;

import jakarta.persistence.EntityNotFoundException;

public class ComprovantePagamentoNotFoundException extends EntityNotFoundException {
    public ComprovantePagamentoNotFoundException(String message) { super(message); }
    public ComprovantePagamentoNotFoundException(String message, Exception exception) { super(message, exception); }
}
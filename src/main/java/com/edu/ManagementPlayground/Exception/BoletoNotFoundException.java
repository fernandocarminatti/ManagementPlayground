package com.edu.ManagementPlayground.Exception;

import jakarta.persistence.EntityNotFoundException;

public class BoletoNotFoundException extends EntityNotFoundException {
    public BoletoNotFoundException(String message) { super(message); }
    public BoletoNotFoundException(String message, Exception exception) { super(message, exception); }
}
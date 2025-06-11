package com.edu.ManagementPlayground.Exception;

import jakarta.persistence.EntityNotFoundException;

public class SupplierNotFoundException extends EntityNotFoundException {
    public SupplierNotFoundException(String message) { super(message); }
    public SupplierNotFoundException(String message, Exception exception) { super(message, exception); }
}
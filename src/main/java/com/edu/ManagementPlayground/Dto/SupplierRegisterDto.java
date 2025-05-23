package com.edu.ManagementPlayground.Dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record SupplierRegisterDto(
        @NotBlank(message = "Name must not be blank")
        String name,
        @NotBlank
        @Length(min=14, max=18)
        String cnpj,
        @NotBlank
        String email,
        @NotBlank
        String phoneNumber) {
}
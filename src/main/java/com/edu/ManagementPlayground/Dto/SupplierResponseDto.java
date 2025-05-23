package com.edu.ManagementPlayground.Dto;

import com.edu.ManagementPlayground.Entity.Supplier;

public record SupplierResponseDto(String name, String cnpj, String email, String phoneNumber) {

    public static SupplierResponseDto fromEntity(Supplier supplier){
        return new SupplierResponseDto(
                supplier.getName(),
                supplier.getCnpj(),
                supplier.getEmail(),
                supplier.getPhoneNumber()
        );
    }
}
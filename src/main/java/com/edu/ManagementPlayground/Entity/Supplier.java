package com.edu.ManagementPlayground.Entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "supplier", schema = "hospital_finance")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "supplier_name")
    private String name;
    @Column(name = "cnpj")
    private String cnpj;
    @Column(name = "email")
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;

    public Supplier() {

    }

    public Supplier(String name, String cnpj, String email, String phoneNumber) {
        this.name = name;
        this.cnpj = cnpj;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return id == supplier.id && Objects.equals(cnpj, supplier.cnpj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cnpj);
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
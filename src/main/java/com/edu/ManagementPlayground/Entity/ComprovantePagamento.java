package com.edu.ManagementPlayground.Entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "comprovante_pagamento", schema = "hospital_finance")
public class ComprovantePagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "payment_date")
    private LocalDate paymentDate;
    @Column(name = "paid_value")
    private BigDecimal value;
    @Column(name = "file_reference")
    private String fileReference;

    @OneToOne
    @JoinColumn(name = "boleto_id")
    private Boleto boleto;

    public ComprovantePagamento() {}

    public ComprovantePagamento(LocalDate paymentDate, BigDecimal value, String fileReference, Boleto boleto) {
        this.paymentDate = paymentDate;
        this.value = value;
        this.fileReference = fileReference;
        this.boleto = boleto;
    }

    public long getId() {
        return id;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getFileReference() {
        return fileReference;
    }

    public Boleto getBoleto() {
        return boleto;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public void setBoleto(Boleto boleto) {
        this.boleto = boleto;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ComprovantePagamento that = (ComprovantePagamento) o;
        return id == that.id && Objects.equals(boleto, that.boleto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, boleto);
    }

    @Override
    public String toString() {
        return "ComprovantePagamento{" +
                "boleto=" + boleto +
                ", fileReference='" + fileReference + '\'' +
                ", value=" + value +
                ", paymentDate=" + paymentDate +
                '}';
    }
}
package com.edu.ManagementPlayground.Entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "boleto", schema = "hospital_finance")
public class Boleto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "typeable_line")
    private String typeableLine;
    @Column(name = "due_date")
    private LocalDate dueDate;
    @Column(name = "total_value")
    private BigDecimal value;
    @Column(name = "payment_status")
    private int paymentStatus;
    @Column(name = "file_reference")
    private String fileReference;

    @ManyToOne
    private NotaFiscal notaFiscal;

    public Boleto() {}

    public Boleto(String typeableLine, LocalDate dueDate, BigDecimal value,  int paymentStatus, String fileReference, NotaFiscal notaFiscal) {
        this.typeableLine = typeableLine;
        this.dueDate = dueDate;
        this.value = value;
        this.paymentStatus = paymentStatus;
        this.fileReference = fileReference;
        this.notaFiscal = notaFiscal;
    }

    public long getId() {
        return id;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getTypeableLine() {
        return typeableLine;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public String getFileReference() {
        return fileReference;
    }

    public NotaFiscal getNotaFiscal() {
        return notaFiscal;
    }

    public void setTypeableLine(String typeableLine) {
        this.typeableLine = typeableLine;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public void setNotaFiscal(NotaFiscal notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Boleto boleto = (Boleto) o;
        return Objects.equals(typeableLine, boleto.typeableLine) && Objects.equals(notaFiscal, boleto.notaFiscal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeableLine, notaFiscal);
    }

    @Override
    public String toString() {
        return "Boleto{" +
                "dueDate=" + dueDate +
                ", value=" + value +
                ", typeableLine='" + typeableLine + '\'' +
                ", paymentStatus=" + paymentStatus +
                ", fileReference='" + fileReference + '\'' +
                ", notaFiscal=" + notaFiscal +
                '}';
    }
}
package com.edu.ManagementPlayground.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "boleto", schema = "hospital_finance")
public class Boleto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "due_date")
    private LocalDate dueDate;
    @Column(name = "total_value")
    private double value;
    @Column(name = "typeable_line")
    private String typeableLine;
    @Column(name = "payment_status")
    private int paymentStatus;
    @Column(name = "file_reference")
    private String fileReference;

    @ManyToOne
    private NotaFiscal notaFiscal;

    public Boleto() {}

    public Boleto(LocalDate dueDate, double value, String typeableLine, int paymentStatus, String fileReference, NotaFiscal notaFiscal) {
        this.dueDate = dueDate;
        this.value = value;
        this.typeableLine = typeableLine;
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

    public double getValue() {
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
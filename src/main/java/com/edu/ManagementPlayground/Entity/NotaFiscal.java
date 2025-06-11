package com.edu.ManagementPlayground.Entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "nota_fiscal", schema = "hospital_finance")
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "number")
    private String numberIdentifier;
    @Column(name = "issue_date")
    private LocalDate issueDate;
    @Column(name = "total_value")
    private BigDecimal totalValue;
    @Column(name = "file_reference")
    private String fileReference;

    @ManyToOne
    private Supplier supplier;

    public NotaFiscal() {}

    public NotaFiscal(String numberIdentifier, LocalDate issueDate, BigDecimal totalValue, String fileReference, Supplier supplier) {
        this.numberIdentifier = numberIdentifier;
        this.issueDate = issueDate;
        this.totalValue = totalValue;
        this.fileReference = fileReference;
        this.supplier = supplier;
    }

    public long getId() {
        return id;
    }

    public String getNumberIdentifier() {
        return numberIdentifier;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public String getFileReference() {
        return fileReference;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNumberIdentifier(String numberIdentifier) {
        this.numberIdentifier = numberIdentifier;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NotaFiscal that = (NotaFiscal) o;
        return id == that.id && Objects.equals(numberIdentifier, that.numberIdentifier) && Objects.equals(supplier, that.supplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numberIdentifier, supplier);
    }

    @Override
    public String toString() {
        return "NotaFiscal{" +
                "numberIdentifier='" + numberIdentifier + '\'' +
                ", issueDate=" + issueDate +
                ", totalValue=" + totalValue +
                ", fileReference='" + fileReference + '\'' +
                ", supplier=" + supplier +
                '}';
    }
}
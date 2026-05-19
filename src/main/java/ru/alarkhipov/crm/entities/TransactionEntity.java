package ru.alarkhipov.crm.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Table(name = "transactions")
@Entity
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private SellerEntity seller;

    @Column(name = "seller_name", nullable = false)
    String sellerName;

    @Column(name = "seller_contact", nullable = false)
    String sellerContact;

    @Column(name = "amount", nullable = false)
    Long amount;

    @Column(name = "payment_type", nullable = false)
    String paymentType;

    @Column(name = "registration_date", nullable = false)
    LocalDateTime transactionDate;

    public TransactionEntity() {
    }

    public TransactionEntity(Long id, SellerEntity seller,
                             String sellerName,
                             String sellerContact,
                             Long amount,
                             String paymentType, LocalDateTime transactionDate) {
        this.id = id;
        this.seller = seller;
        this.sellerName = sellerName;
        this.sellerContact = sellerContact;
        this.amount = amount;
        this.paymentType = paymentType;
        this.transactionDate = transactionDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SellerEntity getSeller() {
        return seller;
    }

    public void setSeller(SellerEntity seller) {
        this.seller = seller;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerContact() {
        return sellerContact;
    }

    public void setSellerContact(String sellerContact) {
        this.sellerContact = sellerContact;
    }
}

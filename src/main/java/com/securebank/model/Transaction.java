package com.securebank.model;

import com.securebank.model.enums.TransactionStatus;
import com.securebank.model.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    private String description;

    @Column(unique = true)
    private String referenceNumber;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }

    public Transaction() {}

    // ===== GETTERS =====
    public Long getId() { return id; }
    public Account getFromAccount() { return fromAccount; }
    public Account getToAccount() { return toAccount; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public TransactionStatus getStatus() { return status; }
    public String getDescription() { return description; }
    public String getReferenceNumber() { return referenceNumber; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // ===== SETTERS =====
    public void setId(Long id) { this.id = id; }
    public void setFromAccount(Account fromAccount) { this.fromAccount = fromAccount; }
    public void setToAccount(Account toAccount) { this.toAccount = toAccount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setType(TransactionType type) { this.type = type; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    // ===== BUILDER =====
    public static TransactionBuilder builder() { return new TransactionBuilder(); }

    public static class TransactionBuilder {
        private Account fromAccount;
        private Account toAccount;
        private BigDecimal amount;
        private TransactionType type;
        private TransactionStatus status;
        private String description;
        private String referenceNumber;

        public TransactionBuilder fromAccount(Account a) { this.fromAccount = a; return this; }
        public TransactionBuilder toAccount(Account a) { this.toAccount = a; return this; }
        public TransactionBuilder amount(BigDecimal a) { this.amount = a; return this; }
        public TransactionBuilder type(TransactionType t) { this.type = t; return this; }
        public TransactionBuilder status(TransactionStatus s) { this.status = s; return this; }
        public TransactionBuilder description(String d) { this.description = d; return this; }
        public TransactionBuilder referenceNumber(String r) { this.referenceNumber = r; return this; }

        public Transaction build() {
            Transaction t = new Transaction();
            t.fromAccount = this.fromAccount;
            t.toAccount = this.toAccount;
            t.amount = this.amount;
            t.type = this.type;
            t.status = this.status;
            t.description = this.description;
            t.referenceNumber = this.referenceNumber;
            return t;
        }
    }
}
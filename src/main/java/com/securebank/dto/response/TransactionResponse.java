package com.securebank.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private Long id;
    private String referenceNumber;
    private String type;
    private String status;
    private BigDecimal amount;
    private String description;
    private String fromAccount;
    private String toAccount;
    private LocalDateTime timestamp;
    private boolean credit; // true = money IN, false = money OUT

    // ===== GETTERS =====
    public Long getId() { return id; }
    public String getReferenceNumber() { return referenceNumber; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isCredit() { return credit; }

    // ===== SETTERS =====
    public void setId(Long id) { this.id = id; }
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    public void setType(String type) { this.type = type; }
    public void setStatus(String status) { this.status = status; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
    public void setFromAccount(String fromAccount) { this.fromAccount = fromAccount; }
    public void setToAccount(String toAccount) { this.toAccount = toAccount; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setCredit(boolean credit) { this.credit = credit; }
}
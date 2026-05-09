package com.securebank.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountResponse {

    private Long id;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private boolean active;
    private LocalDateTime createdAt;
    private String ownerName;
    private String ownerEmail;

    // ===== GETTERS =====
    public Long getId() { return id; }
    public String getAccountNumber() {
        return accountNumber;
    }
    public String getAccountType() {
        return accountType;
    }
    public BigDecimal getBalance() { return balance; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public String getOwnerName() { return ownerName; }
    public String getOwnerEmail() {
        return ownerEmail;
    }

    // ===== SETTERS =====
    public void setId(Long id) { this.id = id; }
    public void setAccountNumber(String n) {
        this.accountNumber = n;
    }
    public void setAccountType(String t) {
        this.accountType = t;
    }
    public void setBalance(BigDecimal b) {
        this.balance = b;
    }
    public void setActive(boolean a) {
        this.active = a;
    }
    public void setCreatedAt(LocalDateTime c) {
        this.createdAt = c;
    }
    public void setOwnerName(String n) {
        this.ownerName = n;
    }
    public void setOwnerEmail(String e) {
        this.ownerEmail = e;
    }
}
package com.securebank.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {

    private String fullName;
    private String email;
    private BigDecimal totalBalance;
    private int totalAccounts;
    private int totalTransactions;
    private int pendingLoans;
    private List<AccountResponse> accounts;

    // ===== GETTERS =====
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public BigDecimal getTotalBalance() {
        return totalBalance;
    }
    public int getTotalAccounts() {
        return totalAccounts;
    }
    public int getTotalTransactions() {
        return totalTransactions;
    }
    public int getPendingLoans() {
        return pendingLoans;
    }
    public List<AccountResponse> getAccounts() {
        return accounts;
    }

    // ===== SETTERS =====
    public void setFullName(String n) {
        this.fullName = n;
    }
    public void setEmail(String e) { this.email = e; }
    public void setTotalBalance(BigDecimal b) {
        this.totalBalance = b;
    }
    public void setTotalAccounts(int t) {
        this.totalAccounts = t;
    }
    public void setTotalTransactions(int t) {
        this.totalTransactions = t;
    }
    public void setPendingLoans(int p) {
        this.pendingLoans = p;
    }
    public void setAccounts(
            List<AccountResponse> accounts) {
        this.accounts = accounts;
    }
}
package com.securebank.model;

import com.securebank.model.enums.AccountType;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@EntityListeners(AuditingEntityListener.class)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 16)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ===== CONSTRUCTORS =====

    public Account() {}

    // ===== GETTERS =====

    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public AccountType getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
    public boolean isActive() { return active; }
    public User getUser() { return user; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ===== SETTERS =====

    public void setId(Long id) { this.id = id; }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setActive(boolean active) { this.active = active; }
    public void setUser(User user) { this.user = user; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ===== BUILDER =====

    public static AccountBuilder builder() { return new AccountBuilder(); }

    public static class AccountBuilder {
        private Long id;
        private String accountNumber;
        private AccountType accountType;
        private BigDecimal balance = BigDecimal.ZERO;
        private boolean active = true;
        private User user;

        public AccountBuilder id(Long id) { this.id = id; return this; }
        public AccountBuilder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber; return this;
        }
        public AccountBuilder accountType(AccountType accountType) {
            this.accountType = accountType; return this;
        }
        public AccountBuilder balance(BigDecimal balance) {
            this.balance = balance; return this;
        }
        public AccountBuilder active(boolean active) {
            this.active = active; return this;
        }
        public AccountBuilder user(User user) {
            this.user = user; return this;
        }

        public Account build() {
            Account account = new Account();
            account.id = this.id;
            account.accountNumber = this.accountNumber;
            account.accountType = this.accountType;
            account.balance = this.balance;
            account.active = this.active;
            account.user = this.user;
            return account;
        }
    }
}
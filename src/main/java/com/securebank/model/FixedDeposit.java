package com.securebank.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fixed_deposits")
public class FixedDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal principalAmount;

    @Column(nullable = false)
    private double interestRate;

    @Column(nullable = false)
    private int tenureMonths;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime maturityDate;

    @Column(nullable = false)
    private boolean active = true;

    @PrePersist
    public void prePersist() {
        this.startDate = LocalDateTime.now();
        this.maturityDate = startDate.plusMonths(tenureMonths);
    }

    public FixedDeposit() {}

    // ===== GETTERS =====
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Account getAccount() { return account; }
    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public double getInterestRate() { return interestRate; }
    public int getTenureMonths() { return tenureMonths; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getMaturityDate() { return maturityDate; }
    public boolean isActive() { return active; }

    // ===== SETTERS =====
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setAccount(Account account) { this.account = account; }
    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public void setTenureMonths(int tenureMonths) { this.tenureMonths = tenureMonths; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public void setMaturityDate(LocalDateTime maturityDate) { this.maturityDate = maturityDate; }
    public void setActive(boolean active) { this.active = active; }

    // ===== BUILDER =====
    public static FixedDepositBuilder builder() { return new FixedDepositBuilder(); }

    public static class FixedDepositBuilder {
        private User user;
        private Account account;
        private BigDecimal principalAmount;
        private double interestRate;
        private int tenureMonths;
        private boolean active = true;

        public FixedDepositBuilder user(User u) { this.user = u; return this; }
        public FixedDepositBuilder account(Account a) { this.account = a; return this; }
        public FixedDepositBuilder principalAmount(BigDecimal p) {
            this.principalAmount = p; return this;
        }
        public FixedDepositBuilder interestRate(double r) { this.interestRate = r; return this; }
        public FixedDepositBuilder tenureMonths(int t) { this.tenureMonths = t; return this; }
        public FixedDepositBuilder active(boolean a) { this.active = a; return this; }

        public FixedDeposit build() {
            FixedDeposit fd = new FixedDeposit();
            fd.user = this.user;
            fd.account = this.account;
            fd.principalAmount = this.principalAmount;
            fd.interestRate = this.interestRate;
            fd.tenureMonths = this.tenureMonths;
            fd.active = this.active;
            return fd;
        }
    }
}
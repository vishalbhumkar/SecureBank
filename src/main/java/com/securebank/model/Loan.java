package com.securebank.model;
import com.securebank.model.enums.LoanStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private int tenureMonths;
    @Column(nullable = false)
    private double interestRate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;
    private String managerRemarks;
    @Column(nullable = false)
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
    @PrePersist
    public void prePersist() {
        this.appliedAt = LocalDateTime.now();
        this.status = LoanStatus.PENDING;
    }
    public Loan() {}
    // ===== GETTERS =====
    public Long getId() { return id; }
    public User getUser() { return user; }
    public BigDecimal getAmount() { return amount; }
    public int getTenureMonths() { return tenureMonths; }
    public double getInterestRate() { return interestRate; }
    public LoanStatus getStatus() { return status; }
    public String getManagerRemarks() { return managerRemarks; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }

    // ===== CALCULATED =====
    public BigDecimal getMonthlyEmi() {
        if (amount == null || tenureMonths == 0) return BigDecimal.ZERO;
        double r = interestRate / 12.0 / 100.0;
        double emi = amount.doubleValue() * r * Math.pow(1 + r, tenureMonths)
                     / (Math.pow(1 + r, tenureMonths) - 1);
        return BigDecimal.valueOf(emi)
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    // ===== SETTERS =====
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setTenureMonths(int tenureMonths) { this.tenureMonths = tenureMonths; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public void setStatus(LoanStatus status) { this.status = status; }
    public void setManagerRemarks(String managerRemarks) { this.managerRemarks = managerRemarks; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    // ===== BUILDER =====
    public static LoanBuilder builder() { return new LoanBuilder(); }
    public static class LoanBuilder {
        private User user;
        private BigDecimal amount;
        private int tenureMonths;
        private double interestRate;
        private LoanStatus status;
        private String managerRemarks;
        public LoanBuilder user(User u) { this.user = u; return this; }
        public LoanBuilder amount(BigDecimal a) { this.amount = a; return this; }
        public LoanBuilder tenureMonths(int t) { this.tenureMonths = t; return this; }
        public LoanBuilder interestRate(double r) { this.interestRate = r; return this; }
        public LoanBuilder status(LoanStatus s) { this.status = s; return this; }
        public LoanBuilder managerRemarks(String m) { this.managerRemarks = m; return this; }
        public Loan build() {
            Loan l = new Loan();
            l.user = this.user;
            l.amount = this.amount;
            l.tenureMonths = this.tenureMonths;
            l.interestRate = this.interestRate;
            l.status = this.status;
            l.managerRemarks = this.managerRemarks;
            return l;
        }
    }
}
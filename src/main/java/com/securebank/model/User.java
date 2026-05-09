package com.securebank.model;

import com.securebank.model.enums.Role;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean active = false;

    @Column(nullable = false)
    private boolean locked = false;

    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    private String phone;

    private String address;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private List<Account> accounts;

    @OneToMany(mappedBy = "user",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private List<Loan> loans;

    // ===== CONSTRUCTORS =====

    public User() {}

    public User(Long id, String fullName, String email, String password,
                Role role, boolean active, boolean locked,
                int failedLoginAttempts, String phone, String address,
                LocalDateTime createdAt, List<Account> accounts,
                List<Loan> loans) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
        this.locked = locked;
        this.failedLoginAttempts = failedLoginAttempts;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
        this.accounts = accounts;
        this.loans = loans;
    }

    // ===== GETTERS =====

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public boolean isLocked() { return locked; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Account> getAccounts() { return accounts; }
    public List<Loan> getLoans() { return loans; }

    // ===== SETTERS =====

    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
    public void setActive(boolean active) { this.active = active; }
    public void setLocked(boolean locked) { this.locked = locked; }
    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
    public void setLoans(List<Loan> loans) { this.loans = loans; }

    // ===== BUILDER =====

    public static UserBuilder builder() { return new UserBuilder(); }

    public static class UserBuilder {
        private Long id;
        private String fullName;
        private String email;
        private String password;
        private Role role;
        private boolean active = false;
        private boolean locked = false;
        private int failedLoginAttempts = 0;
        private String phone;
        private String address;
        private LocalDateTime createdAt;
        private List<Account> accounts;
        private List<Loan> loans;

        public UserBuilder id(Long id) { this.id = id; return this; }
        public UserBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder password(String password) { this.password = password; return this; }
        public UserBuilder role(Role role) { this.role = role; return this; }
        public UserBuilder active(boolean active) { this.active = active; return this; }
        public UserBuilder locked(boolean locked) { this.locked = locked; return this; }
        public UserBuilder failedLoginAttempts(int failedLoginAttempts) {
            this.failedLoginAttempts = failedLoginAttempts; return this;
        }
        public UserBuilder phone(String phone) { this.phone = phone; return this; }
        public UserBuilder address(String address) { this.address = address; return this; }

        public User build() {
            User user = new User();
            user.id = this.id;
            user.fullName = this.fullName;
            user.email = this.email;
            user.password = this.password;
            user.role = this.role;
            user.active = this.active;
            user.locked = this.locked;
            user.failedLoginAttempts = this.failedLoginAttempts;
            user.phone = this.phone;
            user.address = this.address;
            return user;
        }
    }
}
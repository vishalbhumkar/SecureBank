package com.securebank.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_tokens")
public class OtpToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private String purpose;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public OtpToken() {}

    // ===== GETTERS =====
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getOtp() { return otp; }
    public String getPurpose() { return purpose; }
    public boolean isUsed() { return used; }
    public LocalDateTime getExpiresAt() { return expiresAt; }

    // ===== SETTERS =====
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setOtp(String otp) { this.otp = otp; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public void setUsed(boolean used) { this.used = used; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    // ===== BUILDER =====
    public static OtpTokenBuilder builder() { return new OtpTokenBuilder(); }

    public static class OtpTokenBuilder {
        private String email;
        private String otp;
        private String purpose;
        private boolean used = false;
        private LocalDateTime expiresAt;

        public OtpTokenBuilder email(String e) { this.email = e; return this; }
        public OtpTokenBuilder otp(String o) { this.otp = o; return this; }
        public OtpTokenBuilder purpose(String p) { this.purpose = p; return this; }
        public OtpTokenBuilder used(boolean u) { this.used = u; return this; }
        public OtpTokenBuilder expiresAt(LocalDateTime e) { this.expiresAt = e; return this; }

        public OtpToken build() {
            OtpToken token = new OtpToken();
            token.email = this.email;
            token.otp = this.otp;
            token.purpose = this.purpose;
            token.used = this.used;
            token.expiresAt = this.expiresAt;
            return token;
        }
    }
}
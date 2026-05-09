package com.securebank.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String username;

    @Column(nullable = false)
    private String action;

    private String entity;

    @Column(length = 1000)
    private String details;

    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }

    public AuditLog() {}

    // ===== GETTERS =====
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getAction() { return action; }
    public String getEntity() { return entity; }
    public String getDetails() { return details; }
    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // ===== SETTERS =====
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setAction(String action) { this.action = action; }
    public void setEntity(String entity) { this.entity = entity; }
    public void setDetails(String details) { this.details = details; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    // ===== BUILDER =====
    public static AuditLogBuilder builder() { return new AuditLogBuilder(); }

    public static class AuditLogBuilder {
        private Long userId;
        private String username;
        private String action;
        private String entity;
        private String details;
        private String ipAddress;

        public AuditLogBuilder userId(Long u) { this.userId = u; return this; }
        public AuditLogBuilder username(String u) { this.username = u; return this; }
        public AuditLogBuilder action(String a) { this.action = a; return this; }
        public AuditLogBuilder entity(String e) { this.entity = e; return this; }
        public AuditLogBuilder details(String d) { this.details = d; return this; }
        public AuditLogBuilder ipAddress(String i) { this.ipAddress = i; return this; }

        public AuditLog build() {
            AuditLog log = new AuditLog();
            log.userId = this.userId;
            log.username = this.username;
            log.action = this.action;
            log.entity = this.entity;
            log.details = this.details;
            log.ipAddress = this.ipAddress;
            return log;
        }
    }
}
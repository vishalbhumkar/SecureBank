package com.securebank.dto.response;

public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String email;
    private String role;
    private String fullName;

    public JwtResponse(String token, String email,
                       String role, String fullName) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
    }

    // GETTERS
    public String getToken() { return token; }
    public String getType() { return type; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }

    // SETTERS
    public void setToken(String token) { this.token = token; }
    public void setType(String type) { this.type = type; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
package com.securebank.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100,
          message = "Name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 150, message = "Email too long")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50,
          message = "Password must be between 8 and 50 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "Password must contain uppercase, lowercase, number and special character"
    )
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Enter a valid 10-digit Indian mobile number"
    )
    private String phone;

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 500,
          message = "Address must be between 10 and 500 characters")
    private String address;

    // GETTERS
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }

    // SETTERS
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
}
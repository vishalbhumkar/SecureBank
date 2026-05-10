package com.securebank.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class TransferRequest {

    @NotBlank(message = "Recipient account number is required")
    @Size(min = 12, max = 12,
          message = "Account number must be 12 characters")
    private String toAccountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00",
                message = "Minimum transfer amount is Rs.1")
    @DecimalMax(value = "1000000.00",
                message = "Maximum transfer amount is Rs.10,00,000")
    private BigDecimal amount;

    @Size(max = 200,
          message = "Description cannot exceed 200 characters")
    private String description;

    // GETTERS
    public String getToAccountNumber() { return toAccountNumber; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }

    // SETTERS
    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDescription(String description) {
        this.description = description;
    }
}
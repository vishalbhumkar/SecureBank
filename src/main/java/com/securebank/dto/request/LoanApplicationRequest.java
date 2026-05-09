package com.securebank.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class LoanApplicationRequest {

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "1000.00",
                message = "Minimum loan amount is ₹1,000")
    private BigDecimal amount;

    @NotNull(message = "Tenure is required")
    @Min(value = 1, message = "Minimum tenure is 1 month")
    private Integer tenureMonths;

    // Getters
    public BigDecimal getAmount() { return amount; }
    public Integer getTenureMonths() { return tenureMonths; }

    // Setters
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }
}
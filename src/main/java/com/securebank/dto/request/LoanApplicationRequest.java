package com.securebank.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class LoanApplicationRequest {

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "10000.00",
                message = "Minimum loan amount is Rs.10,000")
    @DecimalMax(value = "5000000.00",
                message = "Maximum loan amount is Rs.50,00,000")
    private BigDecimal amount;

    @NotNull(message = "Tenure is required")
    @Min(value = 6, message = "Minimum tenure is 6 months")
    @Max(value = 360, message = "Maximum tenure is 360 months")
    private Integer tenureMonths;

    @NotBlank(message = "Loan type is required")
    private String loanType;

    // GETTERS
    public BigDecimal getAmount() { return amount; }
    public Integer getTenureMonths() { return tenureMonths; }
    public String getLoanType() { return loanType; }

    // SETTERS
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }
    public void setLoanType(String loanType) { this.loanType = loanType; }
}
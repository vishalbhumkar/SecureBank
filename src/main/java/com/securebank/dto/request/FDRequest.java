package com.securebank.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class FDRequest {

    @NotNull(message = "FD amount is required")
    @DecimalMin(value = "1000.00",
                message = "Minimum FD amount is Rs.1,000")
    @DecimalMax(value = "10000000.00",
                message = "Maximum FD amount is Rs.1,00,00,000")
    private BigDecimal amount;

    @NotNull(message = "Tenure is required")
    @Min(value = 1, message = "Minimum tenure is 1 month")
    @Max(value = 120, message = "Maximum tenure is 120 months")
    private Integer tenureMonths;

    // GETTERS
    public BigDecimal getAmount() { return amount; }
    public Integer getTenureMonths() { return tenureMonths; }

    // SETTERS
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }
}
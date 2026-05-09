package com.securebank.dto.request;

public class LoanReviewRequest {

    private Long loanId;
    private String action;   // "APPROVE" or "REJECT"
    private String remarks;

    // ===== GETTERS =====
    public Long getLoanId() { return loanId; }
    public String getAction() { return action; }
    public String getRemarks() { return remarks; }

    // ===== SETTERS =====
    public void setLoanId(Long loanId) { this.loanId = loanId; }
    public void setAction(String action) { this.action = action; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
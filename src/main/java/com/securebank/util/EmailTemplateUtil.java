package com.securebank.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EmailTemplateUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    // ===== WELCOME EMAIL =====
    public String buildWelcomeEmail(String fullName,
                                    String bankName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <style>
                body{font-family:'Segoe UI',Arial,sans-serif;
                     background:#f1f5f9;margin:0;padding:20px}
                .container{max-width:580px;margin:0 auto;
                           background:#fff;border-radius:16px;
                           overflow:hidden;
                           box-shadow:0 4px 24px rgba(0,0,0,0.08)}
                .header{background:linear-gradient(135deg,
                        #1a3c5e,#2563a8);
                        padding:32px;text-align:center;color:#fff}
                .logo{font-size:1.6rem;font-weight:700;
                      letter-spacing:-0.5px}
                .logo span{color:#f0a500}
                .body{padding:32px}
                .title{font-size:1.2rem;font-weight:700;
                       color:#1e293b;margin-bottom:12px}
                .text{color:#64748b;font-size:0.9rem;
                      line-height:1.7;margin-bottom:16px}
                .highlight{background:#f0f9ff;
                           border-left:4px solid #2563a8;
                           padding:14px 16px;border-radius:0 8px 8px 0;
                           margin:20px 0;
                           color:#1e293b;font-size:0.875rem}
                .footer{background:#f8fafc;padding:20px 32px;
                        text-align:center;
                        font-size:0.78rem;color:#94a3b8}
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <div class="logo">Secure<span>Bank</span></div>
                  <div style="margin-top:8px;font-size:0.85rem;
                              opacity:0.85">
                    Your Trusted Banking Partner
                  </div>
                </div>
                <div class="body">
                  <div class="title">
                    Welcome to %s, %s! 🎉
                  </div>
                  <div class="text">
                    Thank you for registering with %s.
                    Your account application has been received
                    and is currently under review by our team.
                  </div>
                  <div class="highlight">
                    ⏳ <strong>What happens next?</strong><br>
                    Our branch manager will review and approve
                    your account within 1-2 business days.
                    You will receive a confirmation email once
                    your account is activated.
                  </div>
                  <div class="text">
                    If you have any questions, please contact
                    our support team.
                  </div>
                </div>
                <div class="footer">
                  © 2024 %s. All rights reserved.<br>
                  This is an automated email, please do not reply.
                </div>
              </div>
            </body>
            </html>
            """.formatted(bankName, fullName, bankName, bankName);
    }

    // ===== ACCOUNT ACTIVATION EMAIL =====
    public String buildActivationEmail(String fullName,
                                       String accountNumber,
                                       String bankName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <style>
                body{font-family:'Segoe UI',Arial,sans-serif;
                     background:#f1f5f9;margin:0;padding:20px}
                .container{max-width:580px;margin:0 auto;
                           background:#fff;border-radius:16px;
                           overflow:hidden;
                           box-shadow:0 4px 24px rgba(0,0,0,0.08)}
                .header{background:linear-gradient(135deg,
                        #16a34a,#22c55e);
                        padding:32px;text-align:center;color:#fff}
                .logo{font-size:1.6rem;font-weight:700}
                .body{padding:32px}
                .title{font-size:1.2rem;font-weight:700;
                       color:#1e293b;margin-bottom:12px}
                .text{color:#64748b;font-size:0.9rem;
                      line-height:1.7;margin-bottom:16px}
                .account-box{background:#f0fdf4;
                             border:2px solid #bbf7d0;
                             border-radius:12px;padding:20px;
                             margin:20px 0;text-align:center}
                .account-label{font-size:0.75rem;
                               color:#16a34a;font-weight:700;
                               text-transform:uppercase;
                               letter-spacing:0.5px;
                               margin-bottom:6px}
                .account-number{font-size:1.4rem;font-weight:700;
                                color:#1e293b;font-family:monospace}
                .footer{background:#f8fafc;padding:20px 32px;
                        text-align:center;
                        font-size:0.78rem;color:#94a3b8}
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <div style="font-size:2.5rem;margin-bottom:8px">
                    ✅
                  </div>
                  <div class="logo">Account Activated!</div>
                </div>
                <div class="body">
                  <div class="title">
                    Congratulations, %s!
                  </div>
                  <div class="text">
                    Your %s account has been successfully
                    activated. You can now login and access
                    all banking services.
                  </div>
                  <div class="account-box">
                    <div class="account-label">
                      Your Account Number
                    </div>
                    <div class="account-number">%s</div>
                  </div>
                  <div class="text">
                    You can now:<br>
                    ✓ Transfer funds to other accounts<br>
                    ✓ Apply for loans<br>
                    ✓ Open Fixed Deposits<br>
                    ✓ View transaction history
                  </div>
                </div>
                <div class="footer">
                  © 2024 %s. All rights reserved.
                </div>
              </div>
            </body>
            </html>
            """.formatted(fullName, bankName,
                          accountNumber, bankName);
    }

    // ===== TRANSACTION ALERT EMAIL =====
    public String buildTransactionEmail(
            String fullName,
            String type,
            BigDecimal amount,
            BigDecimal newBalance,
            String accountNumber,
            String referenceNumber,
            LocalDateTime timestamp,
            String bankName) {

        String typeColor = type.equals("CREDIT")
                ? "#16a34a" : "#dc2626";
        String typeIcon = type.equals("CREDIT")
                ? "💰" : "💸";
        String typeLabel = type.equals("CREDIT")
                ? "Amount Credited" : "Amount Debited";

        return """
            <!DOCTYPE html>
            <html>
            <head>
              <style>
                body{font-family:'Segoe UI',Arial,sans-serif;
                     background:#f1f5f9;margin:0;padding:20px}
                .container{max-width:580px;margin:0 auto;
                           background:#fff;border-radius:16px;
                           overflow:hidden;
                           box-shadow:0 4px 24px rgba(0,0,0,0.08)}
                .header{background:linear-gradient(135deg,
                        #1a3c5e,#2563a8);
                        padding:24px 32px;
                        display:flex;align-items:center;
                        color:#fff}
                .logo{font-size:1.3rem;font-weight:700}
                .logo span{color:#f0a500}
                .body{padding:32px}
                .amount-box{text-align:center;
                            padding:24px;
                            background:#f8fafc;
                            border-radius:12px;margin-bottom:24px}
                .amount-icon{font-size:2.5rem;margin-bottom:8px}
                .amount-label{font-size:0.8rem;font-weight:600;
                              text-transform:uppercase;
                              letter-spacing:0.5px;
                              color:%s;margin-bottom:4px}
                .amount-value{font-size:2rem;font-weight:700;
                              color:%s}
                .details-table{width:100%%;border-collapse:collapse}
                .details-table td{padding:10px 0;
                                   font-size:0.875rem;
                                   border-bottom:1px solid #f1f5f9}
                .detail-label{color:#64748b;width:45%%}
                .detail-value{font-weight:600;
                              color:#1e293b;text-align:right}
                .balance-box{background:#eff6ff;
                             border-radius:10px;
                             padding:14px 16px;margin-top:20px;
                             display:flex;
                             justify-content:space-between;
                             align-items:center}
                .balance-label{font-size:0.8rem;color:#2563a8;
                               font-weight:600}
                .balance-value{font-size:1.1rem;font-weight:700;
                               color:#1e293b}
                .footer{background:#f8fafc;padding:20px 32px;
                        text-align:center;
                        font-size:0.78rem;color:#94a3b8}
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <div class="logo">
                    Secure<span>Bank</span>
                    <div style="font-size:0.75rem;
                                opacity:0.8;font-weight:400;
                                margin-top:2px">
                      Transaction Alert
                    </div>
                  </div>
                </div>
                <div class="body">
                  <p style="color:#1e293b;font-weight:600;
                             margin-bottom:16px">
                    Dear %s,
                  </p>
                  <div class="amount-box">
                    <div class="amount-icon">%s</div>
                    <div class="amount-label">%s</div>
                    <div class="amount-value">
                      ₹%s
                    </div>
                  </div>
                  <table class="details-table">
                    <tr>
                      <td class="detail-label">Account</td>
                      <td class="detail-value"
                          style="font-family:monospace">
                        %s
                      </td>
                    </tr>
                    <tr>
                      <td class="detail-label">
                        Reference No.
                      </td>
                      <td class="detail-value"
                          style="font-family:monospace;
                                 font-size:0.8rem">
                        %s
                      </td>
                    </tr>
                    <tr>
                      <td class="detail-label">Date & Time</td>
                      <td class="detail-value">%s</td>
                    </tr>
                  </table>
                  <div class="balance-box">
                    <span class="balance-label">
                      Available Balance
                    </span>
                    <span class="balance-value">
                      ₹%s
                    </span>
                  </div>
                  <p style="color:#94a3b8;font-size:0.78rem;
                             margin-top:16px">
                    If you did not authorize this transaction,
                    please contact us immediately.
                  </p>
                </div>
                <div class="footer">
                  © 2024 %s. All rights reserved.
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                typeColor, typeColor,
                fullName, typeIcon, typeLabel,
                amount.toPlainString(),
                accountNumber, referenceNumber,
                timestamp.format(FORMATTER),
                newBalance.toPlainString(),
                bankName);
    }

    // ===== OTP EMAIL =====
    public String buildOtpEmail(String fullName,
                                String otp,
                                String purpose,
                                String bankName) {
        String purposeLabel = switch (purpose) {
            case "PASSWORD_RESET" -> "Password Reset";
            case "TRANSFER_CONFIRM" -> "Transfer Confirmation";
            default -> "Verification";
        };

        return """
            <!DOCTYPE html>
            <html>
            <head>
              <style>
                body{font-family:'Segoe UI',Arial,sans-serif;
                     background:#f1f5f9;margin:0;padding:20px}
                .container{max-width:480px;margin:0 auto;
                           background:#fff;border-radius:16px;
                           overflow:hidden;
                           box-shadow:0 4px 24px rgba(0,0,0,0.08)}
                .header{background:linear-gradient(135deg,
                        #1a3c5e,#2563a8);
                        padding:28px;text-align:center;color:#fff}
                .logo{font-size:1.4rem;font-weight:700}
                .logo span{color:#f0a500}
                .body{padding:32px;text-align:center}
                .otp-box{background:#f0f9ff;
                         border:2px dashed #2563a8;
                         border-radius:12px;padding:24px;
                         margin:24px 0}
                .otp-label{font-size:0.78rem;color:#2563a8;
                           font-weight:700;text-transform:uppercase;
                           letter-spacing:0.5px;margin-bottom:8px}
                .otp-code{font-size:2.4rem;font-weight:700;
                          color:#1e293b;letter-spacing:8px;
                          font-family:monospace}
                .expiry{font-size:0.8rem;color:#dc2626;
                        font-weight:600;margin-top:8px}
                .text{color:#64748b;font-size:0.875rem;
                      line-height:1.7}
                .footer{background:#f8fafc;padding:16px 32px;
                        text-align:center;
                        font-size:0.78rem;color:#94a3b8}
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <div class="logo">
                    Secure<span>Bank</span>
                  </div>
                  <div style="margin-top:6px;font-size:0.82rem;
                              opacity:0.85">
                    %s OTP
                  </div>
                </div>
                <div class="body">
                  <p style="color:#1e293b;font-weight:600;
                             margin-bottom:8px">
                    Dear %s,
                  </p>
                  <p class="text">
                    Your One-Time Password for
                    <strong>%s</strong> is:
                  </p>
                  <div class="otp-box">
                    <div class="otp-label">Your OTP</div>
                    <div class="otp-code">%s</div>
                    <div class="expiry">
                      ⏱ Valid for 5 minutes only
                    </div>
                  </div>
                  <p class="text">
                    Never share this OTP with anyone.
                    %s will never ask for your OTP.
                  </p>
                </div>
                <div class="footer">
                  © 2024 %s. All rights reserved.
                </div>
              </div>
            </body>
            </html>
            """.formatted(purposeLabel, fullName,
                          purposeLabel, otp, bankName, bankName);
    }

    // ===== LOAN STATUS EMAIL =====
    public String buildLoanStatusEmail(
            String fullName,
            String status,
            BigDecimal amount,
            String remarks,
            String bankName) {

        boolean approved = "APPROVED".equals(status);
        String headerColor = approved
                ? "linear-gradient(135deg,#16a34a,#22c55e)"
                : "linear-gradient(135deg,#dc2626,#ef4444)";
        String icon = approved ? "✅" : "❌";
        String statusText = approved
                ? "Loan Approved!" : "Loan Rejected";

        return """
            <!DOCTYPE html>
            <html>
            <head>
              <style>
                body{font-family:'Segoe UI',Arial,sans-serif;
                     background:#f1f5f9;margin:0;padding:20px}
                .container{max-width:540px;margin:0 auto;
                           background:#fff;border-radius:16px;
                           overflow:hidden;
                           box-shadow:0 4px 24px rgba(0,0,0,0.08)}
                .header{background:%s;padding:28px;
                        text-align:center;color:#fff}
                .icon{font-size:2.5rem;margin-bottom:8px}
                .status-title{font-size:1.3rem;font-weight:700}
                .body{padding:32px}
                .text{color:#64748b;font-size:0.9rem;
                      line-height:1.7;margin-bottom:16px}
                .detail-box{background:#f8fafc;
                            border-radius:10px;padding:18px;
                            margin:16px 0}
                .detail-row{display:flex;
                            justify-content:space-between;
                            padding:6px 0;font-size:0.875rem;
                            border-bottom:1px solid #f1f5f9}
                .detail-row:last-child{border-bottom:none}
                .detail-label{color:#64748b}
                .detail-value{font-weight:700;color:#1e293b}
                .remarks-box{background:#fffbeb;
                             border:1px solid #fde68a;
                             border-radius:8px;
                             padding:12px 14px;margin-top:14px;
                             font-size:0.83rem;color:#78350f}
                .footer{background:#f8fafc;padding:16px 32px;
                        text-align:center;
                        font-size:0.78rem;color:#94a3b8}
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <div class="icon">%s</div>
                  <div class="status-title">%s</div>
                </div>
                <div class="body">
                  <p style="color:#1e293b;font-weight:600;
                             margin-bottom:8px">
                    Dear %s,
                  </p>
                  <p class="text">
                    %s
                  </p>
                  <div class="detail-box">
                    <div class="detail-row">
                      <span class="detail-label">
                        Loan Amount
                      </span>
                      <span class="detail-value">
                        ₹%s
                      </span>
                    </div>
                    <div class="detail-row">
                      <span class="detail-label">Status</span>
                      <span class="detail-value">%s</span>
                    </div>
                  </div>
                  <div class="remarks-box">
                    📋 <strong>Remarks:</strong>
                    %s
                  </div>
                </div>
                <div class="footer">
                  © 2024 %s. All rights reserved.
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                headerColor, icon, statusText,
                fullName,
                approved
                    ? "Great news! Your loan application has been "
                      + "approved and the amount has been "
                      + "credited to your account."
                    : "We regret to inform you that your loan "
                      + "application could not be approved "
                      + "at this time.",
                amount.toPlainString(),
                status,
                remarks != null ? remarks : "N/A",
                bankName);
    }

    // ===== MONTHLY STATEMENT EMAIL =====
    public String buildMonthlyStatementEmail(
            String fullName,
            String accountNumber,
            String month,
            BigDecimal openingBalance,
            BigDecimal closingBalance,
            int totalTransactions,
            BigDecimal totalCredit,
            BigDecimal totalDebit,
            String bankName) {

        return """
            <!DOCTYPE html>
            <html>
            <head>
              <style>
                body{font-family:'Segoe UI',Arial,sans-serif;
                     background:#f1f5f9;margin:0;padding:20px}
                .container{max-width:580px;margin:0 auto;
                           background:#fff;border-radius:16px;
                           overflow:hidden;
                           box-shadow:0 4px 24px rgba(0,0,0,0.08)}
                .header{background:linear-gradient(135deg,
                        #1a3c5e,#2563a8);
                        padding:28px 32px;color:#fff}
                .logo{font-size:1.4rem;font-weight:700}
                .logo span{color:#f0a500}
                .header-sub{font-size:0.82rem;
                            opacity:0.85;margin-top:4px}
                .body{padding:32px}
                .month-title{font-size:1.1rem;font-weight:700;
                             color:#1e293b;margin-bottom:4px}
                .account-sub{font-size:0.82rem;color:#64748b;
                             font-family:monospace;
                             margin-bottom:24px}
                .stats-grid{display:grid;
                            grid-template-columns:1fr 1fr;
                            gap:12px;margin-bottom:24px}
                .stat-box{padding:16px;border-radius:10px;
                          text-align:center}
                .stat-box.credit{background:#f0fdf4}
                .stat-box.debit{background:#fef2f2}
                .stat-box.balance{background:#eff6ff}
                .stat-box.count{background:#f8fafc}
                .stat-label{font-size:0.72rem;font-weight:700;
                            text-transform:uppercase;
                            letter-spacing:0.5px;margin-bottom:4px}
                .stat-box.credit .stat-label{color:#16a34a}
                .stat-box.debit .stat-label{color:#dc2626}
                .stat-box.balance .stat-label{color:#2563a8}
                .stat-box.count .stat-label{color:#64748b}
                .stat-value{font-size:1.2rem;font-weight:700;
                            color:#1e293b}
                .divider{height:1px;background:#f1f5f9;
                         margin:20px 0}
                .text{color:#64748b;font-size:0.875rem;
                      line-height:1.7}
                .footer{background:#f8fafc;padding:16px 32px;
                        text-align:center;
                        font-size:0.78rem;color:#94a3b8}
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <div class="logo">
                    Secure<span>Bank</span>
                  </div>
                  <div class="header-sub">
                    Monthly Account Statement
                  </div>
                </div>
                <div class="body">
                  <div class="month-title">
                    Statement for %s
                  </div>
                  <div class="account-sub">
                    Account: %s
                  </div>

                  <div class="stats-grid">
                    <div class="stat-box credit">
                      <div class="stat-label">
                        Total Credits
                      </div>
                      <div class="stat-value">
                        ₹%s
                      </div>
                    </div>
                    <div class="stat-box debit">
                      <div class="stat-label">
                        Total Debits
                      </div>
                      <div class="stat-value">
                        ₹%s
                      </div>
                    </div>
                    <div class="stat-box balance">
                      <div class="stat-label">
                        Closing Balance
                      </div>
                      <div class="stat-value">
                        ₹%s
                      </div>
                    </div>
                    <div class="stat-box count">
                      <div class="stat-label">
                        Transactions
                      </div>
                      <div class="stat-value">%d</div>
                    </div>
                  </div>

                  <div class="divider"></div>
                  <p class="text">
                    Dear %s, your monthly account statement
                    for <strong>%s</strong> is summarized
                    above. For a detailed transaction history,
                    please login to your %s account.
                  </p>
                </div>
                <div class="footer">
                  © 2024 %s. All rights reserved.<br>
                  This is an automated statement email.
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                month, accountNumber,
                totalCredit.toPlainString(),
                totalDebit.toPlainString(),
                closingBalance.toPlainString(),
                totalTransactions,
                fullName, month, bankName, bankName);
    }
}
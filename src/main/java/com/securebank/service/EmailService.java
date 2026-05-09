package com.securebank.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation
        .Value;
import org.springframework.mail.javamail
        .JavaMailSender;
import org.springframework.mail.javamail
        .MimeMessageHelper;
import org.springframework.scheduling.annotation
        .Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.bank.name}")
    private String bankName;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter
                    .ofPattern("dd MMM yyyy, HH:mm");

    public EmailService(
            JavaMailSender mailSender,
            TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    // ===== CORE SEND METHOD =====

    @Async
    public void sendHtmlEmail(
            String toEmail,
            String subject,
            String templateName,
            Map<String, Object> variables) {
        try {
            Context ctx = new Context();
            ctx.setVariables(variables);
            ctx.setVariable("bankName", bankName);

            String html = templateEngine
                    .process("emails/"
                            + templateName, ctx);

            MimeMessage message =
                    mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println(
                    "Email failed to: "
                    + toEmail + " — "
                    + e.getMessage());
        }
    }

    // ===== WELCOME EMAIL =====

    @Async
    public void sendWelcomeEmail(
            String toEmail,
            String fullName) {
        sendHtmlEmail(
                toEmail,
                "Welcome to " + bankName
                        + " — Registration Received",
                "welcome",
                Map.of(
                    "fullName", fullName,
                    "email", toEmail
                ));
    }

    // ===== ACCOUNT ACTIVATION =====

    @Async
    public void sendAccountActivationEmail(
            String toEmail,
            String fullName,
            String accountNumber) {
        sendHtmlEmail(
                toEmail,
                bankName
                        + " — Account Activated ✅",
                "account-activation",
                Map.of(
                    "fullName", fullName,
                    "accountNumber", accountNumber,
                    "activatedOn",
                    LocalDateTime.now().format(FMT)
                ));
    }

    // Overload
    @Async
    public void sendAccountActivationEmail(
            String toEmail,
            String fullName) {
        sendAccountActivationEmail(
                toEmail, fullName, "N/A");
    }

    // ===== TRANSACTION ALERT =====

    @Async
    public void sendTransactionAlert(
            String toEmail,
            String fullName,
            String type,
            BigDecimal amount,
            BigDecimal newBalance,
            String accountNumber,
            String referenceNumber,
            LocalDateTime timestamp) {
        sendTransactionAlert(
                toEmail, fullName, type, amount,
                newBalance, accountNumber,
                referenceNumber, timestamp,
                "TRANSFER", null, null);
    }

    @Async
    public void sendTransactionAlert(
            String toEmail,
            String fullName,
            String type,
            BigDecimal amount,
            BigDecimal newBalance,
            String accountNumber,
            String referenceNumber,
            LocalDateTime timestamp,
            String transactionType,
            String fromAccount,
            String toAccount) {

        String subject = bankName + " — "
                + (type.equals("CREDIT")
                        ? "₹" + amount
                              .toPlainString()
                              + " Credited 💰"
                        : "₹" + amount
                              .toPlainString()
                              + " Debited 💸");

        java.util.HashMap<String, Object> vars =
                new java.util.HashMap<>();
        vars.put("fullName", fullName);
        vars.put("type", type);
        vars.put("amount", amount);
        vars.put("newBalance", newBalance);
        vars.put("accountNumber", accountNumber);
        vars.put("referenceNumber",
                referenceNumber);
        vars.put("timestamp",
                timestamp.format(FMT));
        vars.put("transactionType",
                transactionType);
        vars.put("description", "");
        if (fromAccount != null)
            vars.put("fromAccount", fromAccount);
        if (toAccount != null)
            vars.put("toAccount", toAccount);

        sendHtmlEmail(toEmail, subject,
                "transaction-alert", vars);
    }

    // ===== OTP EMAIL =====

    @Async
    public void sendOtpEmail(
            String toEmail,
            String fullName,
            String otp,
            String purpose) {

        String purposeLabel = switch (purpose) {
            case "PASSWORD_RESET" ->
                    "Password Reset";
            case "TRANSFER_CONFIRM" ->
                    "Transfer Confirmation";
            default -> "Verification";
        };

        sendHtmlEmail(
                toEmail,
                bankName + " — Your OTP: " + otp,
                "otp",
                Map.of(
                    "fullName", fullName,
                    "otp", otp,
                    "purposeLabel", purposeLabel
                ));
    }

    // Overload
    @Async
    public void sendOtpEmail(
            String toEmail,
            String otp,
            String purpose) {
        sendOtpEmail(toEmail, "Customer",
                otp, purpose);
    }

    // ===== LOAN STATUS =====

    @Async
    public void sendLoanStatusEmail(
            String toEmail,
            String fullName,
            String status,
            BigDecimal amount,
            String remarks) {
        sendLoanStatusEmail(
                toEmail, fullName, status,
                amount, remarks,
                null, 0, 10.5);
    }

    @Async
    public void sendLoanStatusEmail(
            String toEmail,
            String fullName,
            String status,
            BigDecimal amount,
            String remarks,
            Long loanId,
            int tenureMonths,
            double interestRate) {

        String subject = bankName + " — Loan "
                + ("APPROVED".equals(status)
                        ? "Approved 🎉"
                        : "Application Update");

        java.util.HashMap<String, Object> vars =
                new java.util.HashMap<>();
        vars.put("fullName", fullName);
        vars.put("status", status);
        vars.put("amount", amount);
        vars.put("remarks",
                remarks != null ? remarks : "");
        vars.put("loanId",
                loanId != null ? loanId : 0);
        vars.put("tenureMonths", tenureMonths);
        vars.put("interestRate", interestRate);

        // Calculate EMI details
        if (tenureMonths > 0
                && "APPROVED".equals(status)) {
            double r = interestRate / 12 / 100;
            double n = tenureMonths;
            double p = amount.doubleValue();
            double emi = p * r
                    * Math.pow(1 + r, n)
                    / (Math.pow(1 + r, n) - 1);
            double total = emi * n;
            double interest = total - p;

            vars.put("monthlyEmi",
                    BigDecimal.valueOf(emi)
                        .setScale(2,
                            RoundingMode.HALF_UP)
                        .toPlainString());
            vars.put("totalInterest",
                    BigDecimal.valueOf(interest)
                        .setScale(2,
                            RoundingMode.HALF_UP)
                        .toPlainString());
            vars.put("totalPayable",
                    BigDecimal.valueOf(total)
                        .setScale(2,
                            RoundingMode.HALF_UP)
                        .toPlainString());
        } else {
            vars.put("monthlyEmi", "—");
            vars.put("totalInterest", "—");
            vars.put("totalPayable", "—");
        }

        sendHtmlEmail(toEmail, subject,
                "loan-status", vars);
    }

    // ===== MONTHLY STATEMENT =====

    @Async
    public void sendMonthlyStatement(
            String toEmail,
            String fullName,
            String accountNumber,
            String month,
            BigDecimal openingBalance,
            BigDecimal closingBalance,
            int totalTransactions,
            BigDecimal totalCredit,
            BigDecimal totalDebit) {
        sendMonthlyStatement(
                toEmail, fullName,
                accountNumber, month,
                openingBalance, closingBalance,
                totalTransactions, totalCredit,
                totalDebit, List.of(), 0, 0);
    }

    @Async
    public void sendMonthlyStatement(
            String toEmail,
            String fullName,
            String accountNumber,
            String month,
            BigDecimal openingBalance,
            BigDecimal closingBalance,
            int totalTransactions,
            BigDecimal totalCredit,
            BigDecimal totalDebit,
            List<Map<String, Object>> transactions,
            int creditCount,
            int debitCount) {

        java.util.HashMap<String, Object> vars =
                new java.util.HashMap<>();
        vars.put("fullName", fullName);
        vars.put("email", toEmail);
        vars.put("accountNumber", accountNumber);
        vars.put("month", month);
        vars.put("closingBalance", closingBalance);
        vars.put("totalTransactions",
                totalTransactions);
        vars.put("totalCredit", totalCredit);
        vars.put("totalDebit", totalDebit);
        vars.put("transactions", transactions);
        vars.put("creditCount", creditCount);
        vars.put("debitCount", debitCount);

        sendHtmlEmail(
                toEmail,
                bankName
                        + " — Monthly Statement: "
                        + month,
                "monthly-statement",
                vars);
    }

    // ===== FD MATURITY =====

    @Async
    public void sendFDMaturityEmail(
            String toEmail,
            String fullName,
            BigDecimal principal,
            BigDecimal interest,
            BigDecimal maturityAmount,
            String accountNumber,
            int tenureMonths,
            double interestRate,
            String startDate) {

        sendHtmlEmail(
                toEmail,
                bankName
                        + " — Fixed Deposit Matured 🎉",
                "fd-maturity",
                Map.of(
                    "fullName", fullName,
                    "principal", principal,
                    "interest", interest,
                    "maturityAmount", maturityAmount,
                    "accountNumber", accountNumber,
                    "tenureMonths", tenureMonths,
                    "interestRate", interestRate,
                    "startDate", startDate
                ));
    }

    // ===== PASSWORD CHANGED =====

    @Async
    public void sendPasswordChangedEmail(
            String toEmail,
            String fullName,
            String ipAddress) {

        sendHtmlEmail(
                toEmail,
                bankName
                        + " — Password Changed 🔑",
                "password-changed",
                Map.of(
                    "fullName", fullName,
                    "email", toEmail,
                    "changedOn",
                    LocalDateTime.now().format(FMT),
                    "ipAddress",
                    ipAddress != null
                            ? ipAddress : "Unknown"
                ));
    }

    // ===== ACCOUNT LOCKED =====

    @Async
    public void sendAccountLockedEmail(
            String toEmail,
            String fullName,
            String ipAddress) {

        sendHtmlEmail(
                toEmail,
                bankName
                        + " — Account Locked ⚠️",
                "account-locked",
                Map.of(
                    "fullName", fullName,
                    "lockedAt",
                    LocalDateTime.now().format(FMT),
                    "ipAddress",
                    ipAddress != null
                            ? ipAddress : "Unknown"
                ));
    }
}
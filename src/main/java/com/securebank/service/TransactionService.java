package com.securebank.service;

import com.securebank.dto.request.TransferRequest;
import com.securebank.dto.response.TransactionResponse;
import com.securebank.model.Account;
import com.securebank.model.AuditLog;
import com.securebank.model.Transaction;
import com.securebank.model.User;
import com.securebank.model.enums.TransactionStatus;
import com.securebank.model.enums.TransactionType;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.AuditLogRepository;
import com.securebank.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AuditLogRepository auditLogRepository;
    private final EmailService emailService;
    

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            AuditLogRepository auditLogRepository,
            EmailService emailService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.auditLogRepository = auditLogRepository;
        this.emailService = emailService;
    }

    // ===== FUND TRANSFER =====

    @Transactional
    public Transaction transfer(User sender,
                                TransferRequest request,
                                String ipAddress) {

        // Get sender's first active account
        Account fromAccount = accountRepository.findByUser(sender)
                .stream()
                .filter(Account::isActive)
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("No active account found"));

        // Get recipient account
        Account toAccount = accountRepository
                .findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() ->
                        new RuntimeException("Recipient account not found: "
                                + request.getToAccountNumber()));

        if (fromAccount.getAccountNumber()
                .equals(toAccount.getAccountNumber())) {
            throw new RuntimeException(
                    "Cannot transfer to your own account");
        }

        if (fromAccount.getBalance()
                .compareTo(request.getAmount()) < 0) {
            throw new RuntimeException(
                    "Insufficient balance. Available: ₹"
                            + fromAccount.getBalance());
        }

        // Debit sender
        fromAccount.setBalance(
                fromAccount.getBalance().subtract(request.getAmount()));

        // Credit recipient
        toAccount.setBalance(
                toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Save transaction record
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(
                request.getDescription() != null
                        ? request.getDescription()
                        : "Fund Transfer");
        transaction.setReferenceNumber(generateReference());
        transaction.setTimestamp(LocalDateTime.now());

        Transaction saved = transactionRepository.save(transaction);

        emailService.sendTransactionAlert(
        	    sender.getEmail(),
        	    sender.getFullName(),
        	    "DEBIT",
        	    request.getAmount(),
        	    fromAccount.getBalance(),
        	    fromAccount.getAccountNumber(),
        	    saved.getReferenceNumber(),
        	    saved.getTimestamp(),
        	    "TRANSFER",
        	    null,
        	    toAccount.getAccountNumber()
        	);

        	// Send email alert to recipient
        emailService.sendTransactionAlert(
        	    toAccount.getUser().getEmail(),
        	    toAccount.getUser().getFullName(),
        	    "CREDIT",
        	    request.getAmount(),
        	    toAccount.getBalance(),
        	    toAccount.getAccountNumber(),
        	    saved.getReferenceNumber(),
        	    saved.getTimestamp(),
        	    "TRANSFER",
        	    fromAccount.getAccountNumber(),
        	    null
        	);
        // Audit log
        AuditLog log = new AuditLog();
        log.setUserId(sender.getId());
        log.setUsername(sender.getEmail());
        log.setAction("FUND_TRANSFER");
        log.setEntity("Transaction");
        log.setDetails("Transferred ₹" + request.getAmount()
                + " to " + toAccount.getAccountNumber());
        log.setIpAddress(ipAddress);
        auditLogRepository.save(log);

        return saved;
        
        
        
    }

    // ===== DEPOSIT (by Teller) =====

    @Transactional
    public Transaction deposit(Account account,
                               BigDecimal amount,
                               String description,
                               User performedBy) {
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setToAccount(account);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(description != null
                ? description : "Cash Deposit");
        transaction.setReferenceNumber(generateReference());
        transaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    // ===== WITHDRAWAL (by Teller) =====

    @Transactional
    public Transaction withdraw(Account account,
                                BigDecimal amount,
                                String description,
                                User performedBy) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setFromAccount(account);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(description != null
                ? description : "Cash Withdrawal");
        transaction.setReferenceNumber(generateReference());
        transaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    // ===== GET TRANSACTIONS FOR ACCOUNT =====

    public List<TransactionResponse> getTransactionHistory(
            Account account) {
        return transactionRepository
                .findAllByAccount(account)
                .stream()
                .map(t -> mapToResponse(t, account))
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getRecentTransactions(
            Account account, int limit) {
        return transactionRepository
                .findTop10ByFromAccountOrToAccountOrderByTimestampDesc(
                        account, account)
                .stream()
                .map(t -> mapToResponse(t, account))
                .collect(Collectors.toList());
    }

    // ===== MAPPER =====

    private TransactionResponse mapToResponse(Transaction t,
                                               Account viewerAccount) {
        TransactionResponse response = new TransactionResponse();
        response.setId(t.getId());
        response.setReferenceNumber(t.getReferenceNumber());
        response.setType(t.getType().name());
        response.setStatus(t.getStatus().name());
        response.setAmount(t.getAmount());
        response.setDescription(t.getDescription());
        response.setTimestamp(t.getTimestamp());

        if (t.getFromAccount() != null) {
            response.setFromAccount(t.getFromAccount().getAccountNumber());
        }
        if (t.getToAccount() != null) {
            response.setToAccount(t.getToAccount().getAccountNumber());
        }

        // Credit = money coming IN to viewer's account
        boolean isCredit = t.getToAccount() != null
                && t.getToAccount().getId()
                        .equals(viewerAccount.getId());
        response.setCredit(isCredit);

        return response;
    }

    // ===== HELPER =====

    private String generateReference() {
        return "TXN" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}
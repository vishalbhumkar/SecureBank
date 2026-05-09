package com.securebank.service;

import com.securebank.model.Account;
import com.securebank.model.AuditLog;
import com.securebank.model.Transaction;
import com.securebank.model.User;
import com.securebank.model.enums.TransactionStatus;
import com.securebank.model.enums.TransactionType;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.AuditLogRepository;
import com.securebank.repository.TransactionRepository;
import com.securebank.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TellerService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public TellerService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            AuditLogRepository auditLogRepository,
            UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    // ===== SEARCH ACCOUNT =====

    public Optional<Account> findAccount(String accountNumber) {
        return accountRepository
                .findByAccountNumber(accountNumber);
    }

    // ===== DEPOSIT =====

    @Transactional
    public Transaction deposit(String accountNumber,
                               BigDecimal amount,
                               String description,
                               User teller,
                               String ipAddress) {

        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Account not found: "
                                        + accountNumber));

        if (!account.isActive()) {
            throw new RuntimeException(
                    "Account is inactive");
        }

        account.setBalance(
                account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction txn = new Transaction();
        txn.setToAccount(account);
        txn.setAmount(amount);
        txn.setType(TransactionType.DEPOSIT);
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setDescription(description != null
                && !description.isBlank()
                ? description : "Cash Deposit by Teller");
        txn.setReferenceNumber(generateRef());
        txn.setTimestamp(LocalDateTime.now());
        Transaction saved = transactionRepository.save(txn);

        // Audit
        AuditLog log = new AuditLog();
        log.setUserId(teller.getId());
        log.setUsername(teller.getEmail());
        log.setAction("CASH_DEPOSIT");
        log.setEntity("Transaction");
        log.setDetails("Deposited ₹" + amount
                + " to " + accountNumber);
        log.setIpAddress(ipAddress);
        auditLogRepository.save(log);

        return saved;
    }

    // ===== WITHDRAW =====

    @Transactional
    public Transaction withdraw(String accountNumber,
                                BigDecimal amount,
                                String description,
                                User teller,
                                String ipAddress) {

        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Account not found: "
                                        + accountNumber));

        if (!account.isActive()) {
            throw new RuntimeException("Account is inactive");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException(
                    "Insufficient balance. Available: ₹"
                            + account.getBalance());
        }

        account.setBalance(
                account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction txn = new Transaction();
        txn.setFromAccount(account);
        txn.setAmount(amount);
        txn.setType(TransactionType.WITHDRAWAL);
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setDescription(description != null
                && !description.isBlank()
                ? description : "Cash Withdrawal by Teller");
        txn.setReferenceNumber(generateRef());
        txn.setTimestamp(LocalDateTime.now());
        Transaction saved = transactionRepository.save(txn);

        // Audit
        AuditLog log = new AuditLog();
        log.setUserId(teller.getId());
        log.setUsername(teller.getEmail());
        log.setAction("CASH_WITHDRAWAL");
        log.setEntity("Transaction");
        log.setDetails("Withdrew ₹" + amount
                + " from " + accountNumber);
        log.setIpAddress(ipAddress);
        auditLogRepository.save(log);

        return saved;
    }

    // ===== GET ALL CUSTOMERS =====

    public List<User> getAllActiveCustomers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole().name()
                        .equals("CUSTOMER")
                        && u.isActive())
                .toList();
    }

    // ===== HELPER =====

    private String generateRef() {
        return "TXN" + UUID.randomUUID()
                .toString().replace("-", "")
                .substring(0, 12).toUpperCase();
    }
}
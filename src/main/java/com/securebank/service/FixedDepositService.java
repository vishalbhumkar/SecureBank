package com.securebank.service;

import com.securebank.model.Account;
import com.securebank.model.FixedDeposit;
import com.securebank.model.Transaction;
import com.securebank.model.User;
import com.securebank.model.enums.TransactionStatus;
import com.securebank.model.enums.TransactionType;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.FixedDepositRepository;
import com.securebank.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FixedDepositService {

    private final FixedDepositRepository fdRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public FixedDepositService(
            FixedDepositRepository fdRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {
        this.fdRepository = fdRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // ===== FD INTEREST RATE BY TENURE =====

    public double getInterestRate(int tenureMonths) {
        if (tenureMonths <= 3)  return 4.5;
        if (tenureMonths <= 6)  return 5.5;
        if (tenureMonths <= 12) return 6.5;
        if (tenureMonths <= 24) return 7.0;
        if (tenureMonths <= 36) return 7.5;
        return 8.0; // > 36 months
    }

    // ===== CREATE FD =====

    @Transactional
    public FixedDeposit createFD(User user,
                                 BigDecimal amount,
                                 int tenureMonths) {
        // Get primary active account
        Account account = accountRepository
                .findByUser(user)
                .stream()
                .filter(Account::isActive)
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "No active account found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException(
                    "Insufficient balance. Available: ₹"
                            + account.getBalance());
        }

        // Deduct from account
        account.setBalance(
                account.getBalance().subtract(amount));
        accountRepository.save(account);

        // Record debit transaction
        Transaction txn = new Transaction();
        txn.setFromAccount(account);
        txn.setAmount(amount);
        txn.setType(TransactionType.FD_DEBIT);
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setDescription("Fixed Deposit Created — "
                + tenureMonths + " months");
        txn.setReferenceNumber("FD"
                + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 10)
                    .toUpperCase());
        txn.setTimestamp(LocalDateTime.now());
        transactionRepository.save(txn);

        // Create FD
        double rate = getInterestRate(tenureMonths);
        LocalDateTime start = LocalDateTime.now();

        FixedDeposit fd = new FixedDeposit();
        fd.setUser(user);
        fd.setAccount(account);
        fd.setPrincipalAmount(amount);
        fd.setInterestRate(rate);
        fd.setTenureMonths(tenureMonths);
        fd.setStartDate(start);
        fd.setMaturityDate(start.plusMonths(tenureMonths));
        fd.setActive(true);

        return fdRepository.save(fd);
    }

    // ===== GET FDs BY USER =====

    public List<FixedDeposit> getFDsByUser(User user) {
        return fdRepository.findByUser(user);
    }

    public List<FixedDeposit> getAllActiveFDs() {
        return fdRepository.findByActiveTrue();
    }

    public List<FixedDeposit> getAllFDs() {
        return fdRepository.findAll();
    }

    public Optional<FixedDeposit> findById(Long id) {
        return fdRepository.findById(id);
    }

    public long countActiveFDs() {
        return fdRepository.findByActiveTrue().size();
    }

    // ===== CALCULATE MATURITY AMOUNT =====

    public BigDecimal calculateMaturityAmount(
            BigDecimal principal,
            double rate,
            int tenureMonths) {
        double t = (double) tenureMonths / 12;
        BigDecimal interest = principal
                .multiply(BigDecimal.valueOf(rate / 100 * t))
                .setScale(2, RoundingMode.HALF_UP);
        return principal.add(interest);
    }
}
package com.securebank.scheduler;

import com.securebank.model.Account;
import com.securebank.model.FixedDeposit;
import com.securebank.model.Transaction;
import com.securebank.model.User;
import com.securebank.model.enums.Role;
import com.securebank.model.enums.TransactionStatus;
import com.securebank.model.enums.TransactionType;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.FixedDepositRepository;
import com.securebank.repository.TransactionRepository;
import com.securebank.repository.UserRepository;
import com.securebank.service.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class BankingScheduler {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final FixedDepositRepository fixedDepositRepository;
    private final EmailService emailService;

    public BankingScheduler(
            UserRepository userRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            FixedDepositRepository fixedDepositRepository,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.fixedDepositRepository = fixedDepositRepository;
        this.emailService = emailService;
    }

    // ===== MONTHLY STATEMENT =====
    // Runs on 1st of every month at 8:00 AM

    @Scheduled(cron = "0 0 8 1 * *")
    @Transactional
    public void sendMonthlyStatements() {
        System.out.println("📧 Sending monthly statements...");

        String month = LocalDate.now().minusMonths(1)
                .format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        List<User> customers = userRepository.findByRole(Role.CUSTOMER);

        for (User customer : customers) {
            if (!customer.isActive()) continue;

            List<Account> accounts = accountRepository.findByUser(customer);

            for (Account account : accounts) {
                try {
                    List<Transaction> txns = transactionRepository.findAllByAccount(account);

                    if (txns.isEmpty()) continue;

                    int totalTransactions = txns.size();

                    BigDecimal totalCredit = txns.stream()
                            .filter(t -> t.getToAccount() != null
                                    && t.getToAccount().getId().equals(account.getId()))
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalDebit = txns.stream()
                            .filter(t -> t.getFromAccount() != null
                                    && t.getFromAccount().getId().equals(account.getId()))
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Build transaction list for email
                 // Build transaction list for email
                    List<Map<String, Object>> txnList = txns.stream()
                            .limit(10)
                            .map(t -> {
                                boolean isCredit = t.getToAccount() != null
                                        && t.getToAccount().getId().equals(account.getId());
                                Map<String, Object> map = new java.util.HashMap<>();
                                map.put("date", t.getTimestamp().format(DateTimeFormatter.ofPattern("dd MMM")));
                                map.put("description", t.getDescription() != null ? t.getDescription() : "—");
                                map.put("type", t.getType().name());
                                map.put("amount", t.getAmount().toPlainString());
                                map.put("credit", isCredit);
                                return map;
                            }).toList();

                    int creditCount = (int) txns.stream()
                            .filter(t -> t.getToAccount() != null
                                    && t.getToAccount().getId().equals(account.getId()))
                            .count();

                    int debitCount = totalTransactions - creditCount;

                    emailService.sendMonthlyStatement(
                            customer.getEmail(),
                            customer.getFullName(),
                            account.getAccountNumber(),
                            month,
                            BigDecimal.ZERO,
                            account.getBalance(),
                            txns.size(),
                            totalCredit,
                            totalDebit,
                            txnList,
                            creditCount,
                            debitCount);

                    System.out.println("✅ Statement sent to: " + customer.getEmail());

                } catch (Exception e) {
                    System.err.println("❌ Failed statement for: "
                            + customer.getEmail() + " - " + e.getMessage());
                }
            }
        }

        System.out.println("📧 Monthly statements completed.");
    }

    // ===== FIXED DEPOSIT MATURITY CHECK =====
    // Runs every day at 9:00 AM

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void checkFixedDepositMaturity() {
        System.out.println("🏦 Checking FD maturities...");

        List<FixedDeposit> activeFDs = fixedDepositRepository.findByActiveTrue();

        LocalDateTime now = LocalDateTime.now();

        for (FixedDeposit fd : activeFDs) {
            if (fd.getMaturityDate().isBefore(now)) {
                try {
                    BigDecimal principal = fd.getPrincipalAmount();
                    double rate = fd.getInterestRate();
                    int months = fd.getTenureMonths();

                    double t = (double) months / 12;
                    BigDecimal interest = principal
                            .multiply(BigDecimal.valueOf(rate / 100 * t))
                            .setScale(2, RoundingMode.HALF_UP);
                    BigDecimal maturityAmount = principal.add(interest);

                    // Credit to account
                    Account account = fd.getAccount();
                    account.setBalance(account.getBalance().add(maturityAmount));
                    accountRepository.save(account);

                    // Record transaction
                    Transaction txn = new Transaction();
                    txn.setToAccount(account);
                    txn.setAmount(maturityAmount);
                    txn.setType(TransactionType.DEPOSIT);
                    txn.setStatus(TransactionStatus.SUCCESS);
                    txn.setDescription("FD Maturity Credit — Principal: Rs." + principal
                            + " + Interest: Rs." + interest);
                    txn.setReferenceNumber("FD" + UUID.randomUUID()
                            .toString().replace("-", "").substring(0, 10).toUpperCase());
                    txn.setTimestamp(LocalDateTime.now());
                    transactionRepository.save(txn);

                    // Mark FD as closed
                    fd.setActive(false);
                    fixedDepositRepository.save(fd);

                    // Notify customer
                    emailService.sendFDMaturityEmail(
                            fd.getUser().getEmail(),
                            fd.getUser().getFullName(),
                            principal,
                            interest,
                            maturityAmount,
                            account.getAccountNumber(),
                            fd.getTenureMonths(),
                            fd.getInterestRate(),
                            fd.getStartDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

                    System.out.println("✅ FD matured for: " + fd.getUser().getEmail()
                            + " Amount: Rs." + maturityAmount);

                } catch (Exception e) {
                    System.err.println("❌ FD maturity error: " + e.getMessage());
                }
            }
        }

        System.out.println("🏦 FD maturity check done.");
    }

    // ===== INACTIVE ACCOUNT CLEANUP =====
    // Runs every Sunday at 2:00 AM

    @Scheduled(cron = "0 0 2 * * SUN")
    public void cleanupExpiredOtps() {
        System.out.println("🧹 Cleanup job running...");
        System.out.println("🧹 Cleanup completed.");
    }

    // ===== DAILY HEALTH CHECK =====
    // Runs every day at 6:00 AM

    @Scheduled(cron = "0 0 6 * * *")
    public void dailyHealthCheck() {
        long totalUsers = userRepository.count();
        long totalAccounts = accountRepository.count();
        long totalTxns = transactionRepository.count();

        System.out.println("💚 Daily Health Check — " + LocalDateTime.now());
        System.out.println("   Users: " + totalUsers);
        System.out.println("   Accounts: " + totalAccounts);
        System.out.println("   Transactions: " + totalTxns);
    }
}
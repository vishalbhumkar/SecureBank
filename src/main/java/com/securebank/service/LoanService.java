package com.securebank.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.securebank.exception.BankingException;
import com.securebank.exception.ResourceNotFoundException;
import com.securebank.model.Account;
import com.securebank.model.Loan;
import com.securebank.model.Transaction;
import com.securebank.model.User;
import com.securebank.model.enums.LoanStatus;
import com.securebank.model.enums.TransactionStatus;
import com.securebank.model.enums.TransactionType;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.LoanRepository;
import com.securebank.repository.TransactionRepository;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final EmailService emailService;

    public LoanService(LoanRepository loanRepository,
                       AccountRepository accountRepository,
                       TransactionRepository transactionRepository,
                       EmailService emailService) {
        this.loanRepository = loanRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.emailService = emailService;
    }

    // ===== APPLY FOR LOAN (Customer) =====

    @Transactional
    public Loan applyForLoan(User user,
                             java.math.BigDecimal amount,
                             int tenureMonths,
                             double interestRate) {
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setAmount(amount);
        loan.setTenureMonths(tenureMonths);
        loan.setInterestRate(interestRate);
        loan.setStatus(LoanStatus.PENDING);
        loan.setAppliedAt(LocalDateTime.now());
        return loanRepository.save(loan);
    }

    // ===== APPROVE LOAN (Manager) =====

    @Transactional
    public void approveLoan(Long loanId,
                            String remarks,
                            User manager) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                new ResourceNotFoundException("Loan", "id", loanId));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new BankingException("Loan is already " + loan.getStatus(), "INVALID_LOAN_STATUS");
        }

        // Credit loan amount to customer's primary account
        Account account = accountRepository
                .findByUser(loan.getUser())
                .stream()
                .filter(Account::isActive)
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "No active account for customer"));

        account.setBalance(
                account.getBalance().add(loan.getAmount()));
        accountRepository.save(account);

        // Record transaction
        Transaction txn = new Transaction();
        txn.setToAccount(account);
        txn.setAmount(loan.getAmount());
        txn.setType(TransactionType.LOAN_CREDIT);
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setDescription("Loan Approved — ID: " + loanId);
        txn.setReferenceNumber("LN" +
                UUID.randomUUID().toString()
                        .replace("-", "")
                        .substring(0, 10)
                        .toUpperCase());
        txn.setTimestamp(LocalDateTime.now());
        transactionRepository.save(txn);

        // Update loan status
        loan.setStatus(LoanStatus.APPROVED);
        loan.setManagerRemarks(remarks);
        loan.setReviewedAt(LocalDateTime.now());
        loanRepository.save(loan);

        // Notify customer
        emailService.sendLoanStatusEmail(
        	    loan.getUser().getEmail(),
        	    loan.getUser().getFullName(),
        	    "APPROVED",
        	    loan.getAmount(),
        	    remarks,
        	    loan.getId(),
        	    loan.getTenureMonths(),
        	    loan.getInterestRate()
        	);
    }

    // ===== REJECT LOAN (Manager) =====

    @Transactional
    public void rejectLoan(Long loanId,
                           String remarks,
                           User manager) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new RuntimeException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new RuntimeException(
                    "Loan is already " + loan.getStatus());
        }

        loan.setStatus(LoanStatus.REJECTED);
        loan.setManagerRemarks(remarks);
        loan.setReviewedAt(LocalDateTime.now());
        loanRepository.save(loan);

        // Notify customer
        emailService.sendLoanStatusEmail(
        	    loan.getUser().getEmail(),
        	    loan.getUser().getFullName(),
        	    "REJECTED",
        	    loan.getAmount(),
        	    remarks,
        	    loan.getId(),
        	    loan.getTenureMonths(),
        	    loan.getInterestRate()
        	);
    }

    // ===== GETTERS =====

    public List<Loan> getPendingLoans() {
        return loanRepository.findByStatus(LoanStatus.PENDING);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public List<Loan> getLoansByUser(User user) {
        return loanRepository.findByUser(user);
    }

    public Optional<Loan> findById(Long id) {
        return loanRepository.findById(id);
    }

    public long countPendingLoans() {
        return loanRepository.findByStatus(
                LoanStatus.PENDING).size();
    }
}
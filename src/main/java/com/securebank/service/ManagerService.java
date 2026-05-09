package com.securebank.service;

import com.securebank.model.Account;
import com.securebank.model.User;
import com.securebank.model.enums.AccountType;
import com.securebank.model.enums.Role;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.UserRepository;
import com.securebank.util.AccountNumberUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ManagerService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountNumberUtil accountNumberUtil;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public ManagerService(UserRepository userRepository,
                          AccountRepository accountRepository,
                          AccountNumberUtil accountNumberUtil,
                          EmailService emailService,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.accountNumberUtil = accountNumberUtil;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // ===== GET PENDING CUSTOMERS =====

    public List<User> getPendingCustomers() {
        return userRepository
                .findByActiveAndRole(false, Role.CUSTOMER);
    }

    // ===== GET ALL CUSTOMERS =====

    public List<User> getAllCustomers() {
        return userRepository.findByRole(Role.CUSTOMER);
    }

    // ===== GET ALL TELLERS =====

    public List<User> getAllTellers() {
        return userRepository.findByRole(Role.TELLER);
    }

    // ===== APPROVE CUSTOMER =====

    @Transactional
    public void approveCustomer(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setActive(true);
        userRepository.save(user);

        // Auto-create a SAVINGS account
        Account account = new Account();
        account.setAccountNumber(
                accountNumberUtil.generateUniqueAccountNumber());
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(BigDecimal.ZERO);
        account.setActive(true);
        account.setUser(user);
        accountRepository.save(account);

        emailService.sendAccountActivationEmail(
                user.getEmail(),
                user.getFullName(),
                account.getAccountNumber());
    }

    // ===== REJECT / DEACTIVATE CUSTOMER =====

    @Transactional
    public void deactivateCustomer(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }

    // ===== CREATE TELLER =====

    @Transactional
    public void createTeller(String fullName,
                             String email,
                             String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException(
                    "Email already exists: " + email);
        }

        User teller = new User();
        teller.setFullName(fullName);
        teller.setEmail(email);
        // Default password: Teller@123 (must change on first login)
        teller.setPassword(
                passwordEncoder.encode("Teller@123"));
        teller.setRole(Role.TELLER);
        teller.setActive(true);
        teller.setLocked(false);
        teller.setFailedLoginAttempts(0);
        teller.setPhone(phone);
        userRepository.save(teller);
    }

    // ===== STATS =====

    public long countPendingCustomers() {
        return getPendingCustomers().size();
    }

    public long countAllCustomers() {
        return userRepository.findByRole(Role.CUSTOMER).size();
    }

    public long countAllTellers() {
        return userRepository.findByRole(Role.TELLER).size();
    }

    public Optional<User> findCustomerById(Long id) {
        return userRepository.findById(id);
    }
}
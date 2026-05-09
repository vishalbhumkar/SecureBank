package com.securebank.config;

import com.securebank.model.Account;
import com.securebank.model.User;
import com.securebank.model.enums.AccountType;
import com.securebank.model.enums.Role;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      AccountRepository accountRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // ===== ADMIN =====
        if (!userRepository.existsByEmail("admin@securebank.com")) {
            User admin = new User();
            admin.setFullName("Super Admin");
            admin.setEmail("admin@securebank.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            admin.setLocked(false);
            admin.setFailedLoginAttempts(0);
            userRepository.save(admin);
            System.out.println(
                "✅ Admin: admin@securebank.com / Admin@123");
        }

        // ===== MANAGER =====
        if (!userRepository.existsByEmail("manager@securebank.com")) {
            User manager = new User();
            manager.setFullName("Branch Manager");
            manager.setEmail("manager@securebank.com");
            manager.setPassword(passwordEncoder.encode("Manager@123"));
            manager.setRole(Role.MANAGER);
            manager.setActive(true);
            manager.setLocked(false);
            manager.setFailedLoginAttempts(0);
            userRepository.save(manager);
            System.out.println(
                "✅ Manager: manager@securebank.com / Manager@123");
        }

        // ===== CUSTOMER =====
        if (!userRepository.existsByEmail("customer@securebank.com")) {
            User customer = new User();
            customer.setFullName("John Doe");
            customer.setEmail("customer@securebank.com");
            customer.setPassword(passwordEncoder.encode("Customer@123"));
            customer.setRole(Role.CUSTOMER);
            customer.setActive(true);
            customer.setLocked(false);
            customer.setFailedLoginAttempts(0);
            customer.setPhone("9876543210");
            customer.setAddress("123 MG Road, Pune");
            User savedCustomer = userRepository.save(customer);

            // Create savings account with balance
            Account account = new Account();
            account.setAccountNumber("SB1234567890");
            account.setAccountType(AccountType.SAVINGS);
            account.setBalance(new BigDecimal("50000.00"));
            account.setActive(true);
            account.setUser(savedCustomer);
            accountRepository.save(account);

            System.out.println(
                "✅ Customer: customer@securebank.com / Customer@123");
            System.out.println(
                "✅ Account: SB1234567890 | Balance: ₹50,000");
        }
    }
}
package com.securebank.util;

import com.securebank.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AccountNumberUtil {

    private static final SecureRandom random = new SecureRandom();
    private final AccountRepository accountRepository;

    public AccountNumberUtil(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            // Format: SB + 10 digits
            long number = (long)(Math.random() * 9_000_000_000L) + 1_000_000_000L;
            accountNumber = "SB" + number;
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
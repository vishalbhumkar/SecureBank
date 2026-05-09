package com.securebank.service;

import com.securebank.model.Account;
import com.securebank.model.User;
import com.securebank.model.enums.AccountType;
import com.securebank.repository.AccountRepository;
import com.securebank.util.AccountNumberUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountNumberUtil accountNumberUtil;

    public AccountService(AccountRepository accountRepository,
                          AccountNumberUtil accountNumberUtil) {
        this.accountRepository = accountRepository;
        this.accountNumberUtil = accountNumberUtil;
    }

    @Transactional
    public Account createAccount(User user, AccountType accountType) {
        Account account = new Account();
        account.setAccountNumber(accountNumberUtil.generateUniqueAccountNumber());
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        account.setActive(true);
        account.setUser(user);
        return accountRepository.save(account);
    }

    public List<Account> getAccountsByUser(User user) {
        return accountRepository.findByUser(user);
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public BigDecimal getTotalBalance(User user) {
        return accountRepository.findByUser(user)
                .stream()
                .filter(Account::isActive)
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
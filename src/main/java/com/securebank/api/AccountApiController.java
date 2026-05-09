package com.securebank.api;

import com.securebank.dto.response.AccountResponse;
import com.securebank.dto.response.ApiResponse;
import com.securebank.model.Account;
import com.securebank.model.User;
import com.securebank.service.AccountService;
import com.securebank.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountApiController {

    private final AccountService accountService;
    private final UserService userService;

    public AccountApiController(
            AccountService accountService,
            UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    private User getUser(UserDetails ud) {
        return userService
                .findByEmail(ud.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));
    }

    // ===== GET ALL ACCOUNTS =====

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getMyAccounts(
            @AuthenticationPrincipal UserDetails ud) {

        User user = getUser(ud);

        List<AccountResponse> accounts =
                accountService
                        .getAccountsByUser(user)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(
                ApiResponse.success(accounts,
                        "Accounts retrieved"));
    }

    // ===== GET ACCOUNT BY ID =====

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {

        User user = getUser(ud);

        Account account = accountService
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Account not found"));

        if (!account.getUser().getId()
                .equals(user.getId())) {
            throw new RuntimeException(
                    "Access denied");
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        toResponse(account)));
    }

    // ===== GET TOTAL BALANCE =====

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalBalance(
            @AuthenticationPrincipal UserDetails ud) {

        User user = getUser(ud);
        BigDecimal balance =
                accountService.getTotalBalance(user);

        return ResponseEntity.ok(
                ApiResponse.success(balance,
                        "Total balance retrieved"));
    }

    // ===== MAPPER =====

    private AccountResponse toResponse(Account account) {
        AccountResponse r = new AccountResponse();
        r.setId(account.getId());
        r.setAccountNumber(account.getAccountNumber());
        r.setAccountType(account.getAccountType().name());
        r.setBalance(account.getBalance());
        r.setActive(account.isActive());
        r.setCreatedAt(account.getCreatedAt());
        r.setOwnerName(account.getUser().getFullName());
        r.setOwnerEmail(account.getUser().getEmail());
        return r;
    }
}
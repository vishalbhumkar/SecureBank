package com.securebank.api;

import com.securebank.dto.request.ApiLoginRequest;
import com.securebank.dto.request.RegisterRequest;
import com.securebank.dto.response.ApiResponse;
import com.securebank.dto.response.DashboardResponse;
import com.securebank.dto.response.AccountResponse;
import com.securebank.model.Account;
import com.securebank.model.User;
import com.securebank.service.AccountService;
import com.securebank.service.LoanService;
import com.securebank.service.TransactionService;
import com.securebank.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication
        .AuthenticationManager;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.core
        .annotation.AuthenticationPrincipal;
import org.springframework.security.core
        .userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    private final UserService userService;
    private final AccountService accountService;
    private final LoanService loanService;
    private final TransactionService
            transactionService;
    private final AuthenticationManager
            authenticationManager;

    public AuthApiController(
            UserService userService,
            AccountService accountService,
            LoanService loanService,
            TransactionService transactionService,
            AuthenticationManager
                    authenticationManager) {
        this.userService = userService;
        this.accountService = accountService;
        this.loanService = loanService;
        this.transactionService = transactionService;
        this.authenticationManager =
                authenticationManager;
    }

    // POST /api/v1/auth/register

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {
        try {
            userService.registerCustomer(request);
            return ResponseEntity.ok(
                    ApiResponse.success(
                            request.getEmail(),
                            "Registration successful! "
                            + "Await account approval."));
        } catch (Exception e) {
            throw new RuntimeException(
                    e.getMessage());
        }
    }

    // POST /api/v1/auth/login
    // Session-based login for API

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(
            @RequestBody ApiLoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            return ResponseEntity.ok(
                    ApiResponse.success(
                            Map.of(
                                "email",
                                request.getEmail(),
                                "status",
                                "authenticated"),
                            "Login successful"));
        } catch (Exception e) {
            throw new RuntimeException(
                    "Invalid credentials");
        }
    }

    // GET /api/v1/auth/me — current user info

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(
            @AuthenticationPrincipal UserDetails ud) {

        User user = userService
                .findByEmail(ud.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

        Map<String, Object> info = Map.of(
                "id", user.getId(),
                "fullName", user.getFullName(),
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "active", user.isActive(),
                "locked", user.isLocked()
        );

        return ResponseEntity.ok(
                ApiResponse.success(info));
    }

    // GET /api/v1/auth/dashboard

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @AuthenticationPrincipal UserDetails ud) {

        User user = userService
                .findByEmail(ud.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

        List<Account> accounts =
                accountService.getAccountsByUser(user);

        List<AccountResponse> accountResponses =
                accounts.stream().map(a -> {
                    AccountResponse r =
                            new AccountResponse();
                    r.setId(a.getId());
                    r.setAccountNumber(
                            a.getAccountNumber());
                    r.setAccountType(
                            a.getAccountType().name());
                    r.setBalance(a.getBalance());
                    r.setActive(a.isActive());
                    r.setCreatedAt(a.getCreatedAt());
                    r.setOwnerName(
                            user.getFullName());
                    r.setOwnerEmail(user.getEmail());
                    return r;
                }).toList();

        int totalTxns = accounts.stream()
                .mapToInt(a -> transactionService
                        .getTransactionHistory(a)
                        .size())
                .sum();

        int pendingLoans = (int) loanService
                .getLoansByUser(user)
                .stream()
                .filter(l -> l.getStatus()
                        .name().equals("PENDING"))
                .count();

        DashboardResponse dashboard =
                new DashboardResponse();
        dashboard.setFullName(user.getFullName());
        dashboard.setEmail(user.getEmail());
        dashboard.setTotalBalance(
                accountService.getTotalBalance(user));
        dashboard.setTotalAccounts(accounts.size());
        dashboard.setTotalTransactions(totalTxns);
        dashboard.setPendingLoans(pendingLoans);
        dashboard.setAccounts(accountResponses);

        return ResponseEntity.ok(
                ApiResponse.success(dashboard,
                        "Dashboard data retrieved"));
    }
}
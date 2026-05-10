package com.securebank.api;

import com.securebank.dto.response.AccountResponse;
import com.securebank.dto.response.ApiResponse;
import com.securebank.model.Account;
import com.securebank.model.User;
import com.securebank.service.AccountService;
import com.securebank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account API", description = "Customer account management operations")
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
                        new RuntimeException("User not found"));
    }

    // ===== GET ALL ACCOUNTS =====
    @Operation(
        summary = "Get all accounts",
        description = "Returns all bank accounts belonging to the logged-in customer"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Accounts retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", description = "Unauthorized — please login"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403", description = "Forbidden — insufficient role")
    })
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
                ApiResponse.success(accounts, "Accounts retrieved"));
    }

    // ===== GET ACCOUNT BY ID =====
    @Operation(
        summary = "Get account by ID",
        description = "Returns a specific account by ID — only if it belongs to logged-in customer"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Account found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "Account not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403", description = "Access denied — not your account")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(
            @Parameter(description = "Account ID", required = true, example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        Account account = accountService
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));
        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        return ResponseEntity.ok(
                ApiResponse.success(toResponse(account)));
    }

    // ===== GET TOTAL BALANCE =====
    @Operation(
        summary = "Get total balance",
        description = "Returns the total combined balance across all accounts of logged-in customer"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Balance retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalBalance(
            @AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        BigDecimal balance = accountService.getTotalBalance(user);
        return ResponseEntity.ok(
                ApiResponse.success(balance, "Total balance retrieved"));
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
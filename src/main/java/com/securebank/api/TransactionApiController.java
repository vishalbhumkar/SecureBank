package com.securebank.api;

import com.securebank.dto.request.TransferRequest;
import com.securebank.dto.response.ApiResponse;
import com.securebank.dto.response
        .TransactionResponse;
import com.securebank.model.Account;
import com.securebank.model.Transaction;
import com.securebank.model.User;
import com.securebank.service.AccountService;
import com.securebank.service.TransactionService;
import com.securebank.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core
        .annotation.AuthenticationPrincipal;
import org.springframework.security.core
        .userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionApiController {

    private final TransactionService
            transactionService;
    private final AccountService accountService;
    private final UserService userService;

    public TransactionApiController(
            TransactionService transactionService,
            AccountService accountService,
            UserService userService) {
        this.transactionService = transactionService;
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

    // GET /api/v1/transactions
    // Returns transaction history for primary account

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(required = false) Long accountId) {

        User user = getUser(ud);
        List<Account> accounts =
                accountService.getAccountsByUser(user);

        if (accounts.isEmpty()) {
            throw new RuntimeException(
                    "No accounts found");
        }

        Account account;
        if (accountId != null) {
            account = accountService
                    .findById(accountId)
                    .orElse(accounts.get(0));
        } else {
            account = accounts.stream()
                    .filter(Account::isActive)
                    .findFirst()
                    .orElse(accounts.get(0));
        }

        List<TransactionResponse> txns =
                transactionService
                        .getTransactionHistory(account);

        return ResponseEntity.ok(
                ApiResponse.success(txns,
                        txns.size()
                        + " transactions found"));
    }

    // POST /api/v1/transactions/transfer

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<String>> transfer(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @RequestBody TransferRequest request,
            HttpServletRequest httpRequest) {

        User user = getUser(ud);
        Transaction txn = transactionService
                .transfer(user, request,
                        httpRequest.getRemoteAddr());

        return ResponseEntity.ok(
                ApiResponse.success(
                        txn.getReferenceNumber(),
                        "Transfer successful! Ref: "
                        + txn.getReferenceNumber()));
    }

    // GET /api/v1/transactions/recent

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getRecent(
            @AuthenticationPrincipal UserDetails ud) {


        User user = getUser(ud);
        List<Account> accounts =
                accountService.getAccountsByUser(user);

        if (accounts.isEmpty()) {
            throw new RuntimeException(
                    "No accounts found");
        }

        Account account = accounts.stream()
                .filter(Account::isActive)
                .findFirst()
                .orElse(accounts.get(0));

        List<TransactionResponse> txns =
                transactionService
                        .getRecentTransactions(
                                account, 10);

        return ResponseEntity.ok(
                ApiResponse.success(txns));
    }
}
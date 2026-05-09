package com.securebank.api;

import com.securebank.dto.request
        .LoanApplicationRequest;
import com.securebank.dto.response.ApiResponse;
import com.securebank.model.Loan;
import com.securebank.model.User;
import com.securebank.service.LoanService;
import com.securebank.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core
        .annotation.AuthenticationPrincipal;
import org.springframework.security.core
        .userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanApiController {

    private final LoanService loanService;
    private final UserService userService;

    public LoanApiController(
            LoanService loanService,
            UserService userService) {
        this.loanService = loanService;
        this.userService = userService;
    }

    private User getUser(UserDetails ud) {
        return userService
                .findByEmail(ud.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));
    }

    // GET /api/v1/loans — get my loans

    @GetMapping
    public ResponseEntity<ApiResponse<List<Loan>>> getMyLoans(
            @AuthenticationPrincipal UserDetails ud) {

        User user = getUser(ud);
        List<Loan> loans =
                loanService.getLoansByUser(user);

        return ResponseEntity.ok(
                ApiResponse.success(loans,
                        loans.size()
                        + " loans found"));
    }

    // POST /api/v1/loans/apply

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<String>> applyLoan(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @RequestBody LoanApplicationRequest request) {

        User user = getUser(ud);

        Loan loan = loanService.applyForLoan(
                user,
                request.getAmount(),
                request.getTenureMonths(),
                10.5);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Loan #" + loan.getId(),
                        "Loan application submitted "
                        + "successfully!"));
    }

    // GET /api/v1/loans/{id}

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Loan>> getLoanById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {

        User user = getUser(ud);
        Loan loan = loanService.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Loan not found"));

        if (!loan.getUser().getId()
                .equals(user.getId())) {
            throw new RuntimeException(
                    "Access denied");
        }

        return ResponseEntity.ok(
                ApiResponse.success(loan));
    }
}
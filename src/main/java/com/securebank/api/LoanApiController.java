package com.securebank.api;

import com.securebank.dto.request.LoanApplicationRequest;
import com.securebank.dto.response.ApiResponse;
import com.securebank.model.Loan;
import com.securebank.model.User;
import com.securebank.service.LoanService;
import com.securebank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@Tag(name = "Loan API", description = "Loan application and management operations")
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
                        new RuntimeException("User not found"));
    }

    // GET /api/v1/loans
    @Operation(
        summary = "Get my loans",
        description = "Returns all loan applications submitted by the logged-in customer including status and remarks."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Loans retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", description = "Unauthorized — please login"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403", description = "Forbidden — insufficient role")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<Loan>>> getMyLoans(
            @AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        List<Loan> loans = loanService.getLoansByUser(user);
        return ResponseEntity.ok(
                ApiResponse.success(loans, loans.size() + " loans found"));
    }

    // POST /api/v1/loans/apply
    @Operation(
        summary = "Apply for a loan",
        description = "Submits a new loan application. Default interest rate is 10.5% p.a. Requires manager approval."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Loan application submitted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", description = "Validation error — check amount and tenure"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", description = "Unauthorized — please login"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403", description = "Forbidden — only customers can apply")
    })
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
                        "Loan application submitted successfully!"));
    }

    // GET /api/v1/loans/{id}
    @Operation(
        summary = "Get loan by ID",
        description = "Returns details of a specific loan by ID. Only accessible by the loan owner."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Loan found successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "Loan not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403", description = "Access denied — not your loan"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Loan>> getLoanById(
            @Parameter(description = "Loan ID", required = true, example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        Loan loan = loanService.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Loan not found"));
        if (!loan.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        return ResponseEntity.ok(ApiResponse.success(loan));
    }
}
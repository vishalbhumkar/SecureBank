package com.securebank.controller.customer;

import com.securebank.dto.request.LoanApplicationRequest;
import com.securebank.model.User;
import com.securebank.service.LoanService;
import com.securebank.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer/loans")
public class CustomerLoanController {

    private final LoanService loanService;
    private final UserService userService;

    public CustomerLoanController(LoanService loanService,
                                  UserService userService) {
        this.loanService = loanService;
        this.userService = userService;
    }

    private User getLoggedInUser(UserDetails ud) {
        return userService.findByEmail(ud.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    @GetMapping
    public String loansPage(
            @AuthenticationPrincipal UserDetails ud,
            Model model) {
        User user = getLoggedInUser(ud);
        model.addAttribute("user", user);
        model.addAttribute("loans",
                loanService.getLoansByUser(user));
        model.addAttribute("loanRequest",
                new LoanApplicationRequest());
        return "customer/loans";
    }

    @PostMapping("/apply")
    public String applyLoan(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @ModelAttribute("loanRequest")
            LoanApplicationRequest request,
            BindingResult result,
            RedirectAttributes ra,
            Model model) {

        User user = getLoggedInUser(ud);

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("loans",
                    loanService.getLoansByUser(user));
            return "customer/loans";
        }

        try {
            // Fixed interest rate of 10.5%
            loanService.applyForLoan(
                    user,
                    request.getAmount(),
                    request.getTenureMonths(),
                    10.5);
            ra.addFlashAttribute("successMsg",
                    "Loan application submitted! "
                    + "Awaiting manager approval.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/customer/loans";
    }
}
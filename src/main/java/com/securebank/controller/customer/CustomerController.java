package com.securebank.controller.customer;

import com.securebank.dto.request.TransferRequest;
import com.securebank.model.Account;
import com.securebank.model.Transaction;
import com.securebank.model.User;
import com.securebank.service.AccountService;
import com.securebank.service.TransactionService;
import com.securebank.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public CustomerController(
            UserService userService,
            AccountService accountService,
            TransactionService transactionService) {
        this.userService = userService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    private User getLoggedInUser(UserDetails ud) {
        return userService.findByEmail(ud.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));
    }

    // ===== DASHBOARD =====

    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal UserDetails ud,
            Model model) {

        User user = getLoggedInUser(ud);
        List<Account> accounts =
                accountService.getAccountsByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("totalBalance",
                accountService.getTotalBalance(user));

        if (!accounts.isEmpty()) {
            Account primary = accounts.stream()
                    .filter(Account::isActive)
                    .findFirst()
                    .orElse(accounts.get(0));

            model.addAttribute("recentTransactions",
                    transactionService
                            .getRecentTransactions(
                                    primary, 10));
            model.addAttribute("primaryAccount", primary);
        }

        return "customer/dashboard";
    }

    // ===== TRANSFER =====

    @GetMapping("/transfer")
    public String transferPage(
            @AuthenticationPrincipal UserDetails ud,
            Model model) {

        User user = getLoggedInUser(ud);
        model.addAttribute("transferRequest",
                new TransferRequest());
        model.addAttribute("accounts",
                accountService.getAccountsByUser(user));
        model.addAttribute("user", user);
        return "customer/transfer";
    }

    @PostMapping("/transfer")
    public String processTransfer(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @ModelAttribute("transferRequest")
            TransferRequest request,
            BindingResult result,
            HttpServletRequest httpRequest,
            RedirectAttributes ra,
            Model model) {

        User user = getLoggedInUser(ud);

        if (result.hasErrors()) {
            model.addAttribute("accounts",
                    accountService.getAccountsByUser(user));
            model.addAttribute("user", user);
            return "customer/transfer";
        }

        try {
            String ip = httpRequest.getRemoteAddr();
            Transaction txn = transactionService
                    .transfer(user, request, ip);
            ra.addFlashAttribute("successMsg",
                    "Transfer successful! Reference: "
                            + txn.getReferenceNumber());
            return "redirect:/customer/dashboard";
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("accounts",
                    accountService.getAccountsByUser(user));
            model.addAttribute("user", user);
            return "customer/transfer";
        }
    }

    // ===== TRANSACTIONS =====

    @GetMapping("/transactions")
    public String transactionHistory(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(required = false) Long accountId,
            Model model) {

        User user = getLoggedInUser(ud);
        List<Account> accounts =
                accountService.getAccountsByUser(user);

        Account selected;
        if (accountId != null) {
            selected = accountService
                    .findById(accountId)
                    .orElse(accounts.isEmpty()
                            ? null : accounts.get(0));
        } else {
            selected = accounts.stream()
                    .filter(Account::isActive)
                    .findFirst()
                    .orElse(accounts.isEmpty()
                            ? null : accounts.get(0));
        }

        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("selectedAccount", selected);

        if (selected != null) {
            model.addAttribute("transactions",
                    transactionService
                            .getTransactionHistory(selected));
        }

        return "customer/transactions";
    }

    // ===== PROFILE =====

    @GetMapping("/profile")
    public String profile(
            @AuthenticationPrincipal UserDetails ud,
            Model model) {

        User user = getLoggedInUser(ud);
        List<Account> accounts =
                accountService.getAccountsByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("totalBalance",
                accountService.getTotalBalance(user));
        return "customer/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String address,
            RedirectAttributes ra) {

        User user = getLoggedInUser(ud);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setAddress(address);
        userService.saveUser(user);

        ra.addFlashAttribute("successMsg",
                "Profile updated successfully!");
        return "redirect:/customer/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes ra) {

        User user = getLoggedInUser(ud);

        try {
            userService.changePassword(
                    user, currentPassword,
                    newPassword, confirmPassword);
            ra.addFlashAttribute("successMsg",
                    "Password changed successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg",
                    e.getMessage());
        }

        return "redirect:/customer/profile";
    }
}
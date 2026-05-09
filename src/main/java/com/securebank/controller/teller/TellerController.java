package com.securebank.controller.teller;

import com.securebank.model.Account;
import com.securebank.model.Transaction;
import com.securebank.model.User;
import com.securebank.service.TellerService;
import com.securebank.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/teller")
public class TellerController {

    private final TellerService tellerService;
    private final UserService userService;

    public TellerController(TellerService tellerService,
                            UserService userService) {
        this.tellerService = tellerService;
        this.userService = userService;
    }

    private User getLoggedInUser(UserDetails ud) {
        return userService.findByEmail(ud.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // ===== DASHBOARD =====

    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal UserDetails ud,
            Model model) {
        model.addAttribute("teller", getLoggedInUser(ud));
        model.addAttribute("customers",
                tellerService.getAllActiveCustomers());
        return "teller/dashboard";
    }

    // ===== SEARCH ACCOUNT =====

    @GetMapping("/search")
    public String searchAccount(
            @RequestParam String accountNumber,
            @RequestParam String action,
            Model model,
            @AuthenticationPrincipal UserDetails ud) {

        model.addAttribute("teller", getLoggedInUser(ud));
        model.addAttribute("action", action);
        model.addAttribute("accountNumber", accountNumber);

        Optional<Account> account =
                tellerService.findAccount(accountNumber);

        if (account.isPresent()) {
            model.addAttribute("account", account.get());
        } else {
            model.addAttribute("errorMsg",
                    "Account not found: " + accountNumber);
        }

        return "deposit".equals(action)
                ? "teller/deposit"
                : "teller/withdraw";
    }

    // ===== DEPOSIT =====

    @GetMapping("/deposit")
    public String depositPage(
            @AuthenticationPrincipal UserDetails ud,
            Model model) {
        model.addAttribute("teller", getLoggedInUser(ud));
        return "teller/deposit";
    }

    @PostMapping("/deposit")
    public String processDeposit(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description,
            @AuthenticationPrincipal UserDetails ud,
            HttpServletRequest request,
            RedirectAttributes ra) {
        try {
            User teller = getLoggedInUser(ud);
            Transaction txn = tellerService.deposit(
                    accountNumber, amount, description,
                    teller, request.getRemoteAddr());

            ra.addFlashAttribute("successMsg",
                    "Deposit successful! Ref: "
                            + txn.getReferenceNumber());
            return "redirect:/teller/dashboard";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/teller/deposit";
        }
    }

    // ===== WITHDRAW =====

    @GetMapping("/withdraw")
    public String withdrawPage(
            @AuthenticationPrincipal UserDetails ud,
            Model model) {
        model.addAttribute("teller", getLoggedInUser(ud));
        return "teller/withdraw";
    }

    @PostMapping("/withdraw")
    public String processWithdraw(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description,
            @AuthenticationPrincipal UserDetails ud,
            HttpServletRequest request,
            RedirectAttributes ra) {
        try {
            User teller = getLoggedInUser(ud);
            Transaction txn = tellerService.withdraw(
                    accountNumber, amount, description,
                    teller, request.getRemoteAddr());

            ra.addFlashAttribute("successMsg",
                    "Withdrawal successful! Ref: "
                            + txn.getReferenceNumber());
            return "redirect:/teller/dashboard";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/teller/withdraw";
        }
    }
}
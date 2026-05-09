package com.securebank.controller.customer;

import com.securebank.dto.request.FDRequest;
import com.securebank.model.Account;
import com.securebank.model.User;
import com.securebank.service.AccountService;
import com.securebank.service.FixedDepositService;
import com.securebank.service.PdfStatementService;
import com.securebank.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation
        .AuthenticationPrincipal;
import org.springframework.security.core.userdetails
        .UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support
        .RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerFDController {

    private final UserService userService;
    private final AccountService accountService;
    private final FixedDepositService fdService;
    private final PdfStatementService pdfService;

    public CustomerFDController(
            UserService userService,
            AccountService accountService,
            FixedDepositService fdService,
            PdfStatementService pdfService) {
        this.userService = userService;
        this.accountService = accountService;
        this.fdService = fdService;
        this.pdfService = pdfService;
    }

    private User getUser(UserDetails ud) {
        return userService.findByEmail(ud.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));
    }

    // ===== FIXED DEPOSITS =====

    @GetMapping("/fixed-deposits")
    public String fdPage(
            @AuthenticationPrincipal UserDetails ud,
            Model model) {

        User user = getUser(ud);
        List<Account> accounts =
                accountService.getAccountsByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("totalBalance",
                accountService.getTotalBalance(user));
        model.addAttribute("fds",
                fdService.getFDsByUser(user));
        model.addAttribute("fdRequest",
                new FDRequest());
        model.addAttribute("fdService", fdService);

        return "customer/fixed-deposits";
    }

    @PostMapping("/fixed-deposits/create")
    public String createFD(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @ModelAttribute("fdRequest")
            FDRequest request,
            BindingResult result,
            RedirectAttributes ra,
            Model model) {

        User user = getUser(ud);

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("accounts",
                    accountService.getAccountsByUser(user));
            model.addAttribute("fds",
                    fdService.getFDsByUser(user));
            return "customer/fixed-deposits";
        }

        try {
            fdService.createFD(user,
                    request.getAmount(),
                    request.getTenureMonths());
            ra.addFlashAttribute("successMsg",
                    "Fixed Deposit created successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg",
                    e.getMessage());
        }

        return "redirect:/customer/fixed-deposits";
    }

    // ===== PDF STATEMENT DOWNLOAD =====

    @GetMapping("/statement/download")
    public ResponseEntity<byte[]> downloadStatement(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(required = false) Long accountId) {

        User user = getUser(ud);
        List<Account> accounts =
                accountService.getAccountsByUser(user);

        if (accounts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Account account;
        if (accountId != null) {
            account = accountService.findById(accountId)
                    .orElse(accounts.get(0));
        } else {
            account = accounts.stream()
                    .filter(Account::isActive)
                    .findFirst()
                    .orElse(accounts.get(0));
        }

        try {
            byte[] pdf = pdfService
                    .generateStatement(user, account);

            String filename = "SecureBank_Statement_"
                    + account.getAccountNumber()
                    + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\""
                                    + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError().build();
        }
    }
}
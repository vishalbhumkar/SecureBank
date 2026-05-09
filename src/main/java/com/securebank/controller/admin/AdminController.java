package com.securebank.controller.admin;

import com.securebank.model.User;
import com.securebank.model.enums.Role;
import com.securebank.model.enums.TransactionStatus;
import com.securebank.model.enums.TransactionType;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.AuditLogRepository;
import com.securebank.repository.FixedDepositRepository;
import com.securebank.repository.LoanRepository;
import com.securebank.repository.TransactionRepository;
import com.securebank.repository.UserRepository;
import com.securebank.service.UserService;
import org.springframework.security.core.annotation
        .AuthenticationPrincipal;
import org.springframework.security.core.userdetails
        .UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support
        .RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository
            transactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final LoanRepository loanRepository;
    private final FixedDepositRepository fdRepository;
    private final UserService userService;

    public AdminController(
            UserRepository userRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            AuditLogRepository auditLogRepository,
            LoanRepository loanRepository,
            FixedDepositRepository fdRepository,
            UserService userService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogRepository = auditLogRepository;
        this.loanRepository = loanRepository;
        this.fdRepository = fdRepository;
        this.userService = userService;
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

        model.addAttribute("admin",
                getLoggedInUser(ud));

        // User stats
        model.addAttribute("totalUsers",
                userRepository.count());
        model.addAttribute("totalCustomers",
                userRepository.findByRole(
                        Role.CUSTOMER).size());
        model.addAttribute("totalManagers",
                userRepository.findByRole(
                        Role.MANAGER).size());
        model.addAttribute("totalTellers",
                userRepository.findByRole(
                        Role.TELLER).size());

        // Financial stats
        model.addAttribute("totalAccounts",
                accountRepository.count());
        model.addAttribute("totalTransactions",
                transactionRepository.count());
        model.addAttribute("totalLoans",
                loanRepository.count());
        model.addAttribute("totalFDs",
                fdRepository.count());

        // Total deposits across all accounts
        BigDecimal totalDeposits =
                accountRepository.findAll()
                        .stream()
                        .map(a -> a.getBalance())
                        .reduce(BigDecimal.ZERO,
                                BigDecimal::add);
        model.addAttribute("totalDeposits",
                totalDeposits);

        // Recent data
        model.addAttribute("recentUsers",
                userRepository.findAll()
                        .stream()
                        .sorted((a, b) -> {
                            if (a.getCreatedAt() == null)
                                return 1;
                            if (b.getCreatedAt() == null)
                                return -1;
                            return b.getCreatedAt()
                                    .compareTo(
                                            a.getCreatedAt());
                        })
                        .limit(6)
                        .toList());

        model.addAttribute("recentLogs",
                auditLogRepository
                        .findTop50ByOrderByTimestampDesc()
                        .stream().limit(6).toList());

        model.addAttribute("recentTransactions",
                transactionRepository.findAll()
                        .stream()
                        .sorted((a, b) ->
                                b.getTimestamp().compareTo(
                                        a.getTimestamp()))
                        .limit(6).toList());

        return "admin/dashboard";
    }

    // ===== USERS =====

    @GetMapping("/users")
    public String users(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(defaultValue = "all")
            String role,
            Model model) {

        model.addAttribute("admin",
                getLoggedInUser(ud));
        model.addAttribute("roleFilter", role);

        if ("all".equals(role)) {
            model.addAttribute("users",
                    userRepository.findAll());
        } else {
            try {
                Role r = Role.valueOf(
                        role.toUpperCase());
                model.addAttribute("users",
                        userRepository.findByRole(r));
            } catch (Exception e) {
                model.addAttribute("users",
                        userRepository.findAll());
            }
        }
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(
            @PathVariable Long id,
            RedirectAttributes ra) {
        userRepository.findById(id).ifPresent(u -> {
            u.setActive(!u.isActive());
            if (u.isActive()) {
                u.setLocked(false);
                u.setFailedLoginAttempts(0);
            }
            userRepository.save(u);
        });
        ra.addFlashAttribute("successMsg",
                "User status updated.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/unlock")
    public String unlockUser(
            @PathVariable Long id,
            RedirectAttributes ra) {
        userRepository.findById(id).ifPresent(u -> {
            u.setLocked(false);
            u.setFailedLoginAttempts(0);
            userRepository.save(u);
        });
        ra.addFlashAttribute("successMsg",
                "User account unlocked.");
        return "redirect:/admin/users";
    }

    // ===== AUDIT LOGS =====

    @GetMapping("/audit-logs")
    public String auditLogs(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(defaultValue = "all")
            String action,
            Model model) {

        model.addAttribute("admin",
                getLoggedInUser(ud));
        model.addAttribute("actionFilter", action);

        var allLogs = auditLogRepository
                .findTop50ByOrderByTimestampDesc();

        if (!"all".equals(action)) {
            allLogs = allLogs.stream()
                    .filter(l -> action.equals(
                            l.getAction()))
                    .toList();
        }

        model.addAttribute("logs", allLogs);

        // Distinct actions for filter
        model.addAttribute("distinctActions",
                auditLogRepository
                        .findTop50ByOrderByTimestampDesc()
                        .stream()
                        .map(l -> l.getAction())
                        .distinct()
                        .sorted()
                        .toList());

        return "admin/audit-logs";
    }

    // ===== TRANSACTIONS =====

    @GetMapping("/transactions")
    public String transactions(
            @AuthenticationPrincipal UserDetails ud,
            Model model) {
        model.addAttribute("admin",
                getLoggedInUser(ud));
        model.addAttribute("transactions",
                transactionRepository.findAll()
                        .stream()
                        .sorted((a, b) ->
                                b.getTimestamp()
                                 .compareTo(
                                         a.getTimestamp()))
                        .limit(100)
                        .toList());
        return "admin/transactions";
    }

    // ===== REPORTS =====

    @GetMapping("/reports")
    public String reports(
            @AuthenticationPrincipal UserDetails ud,
            Model model) {

        model.addAttribute("admin",
                getLoggedInUser(ud));

        // Transaction type breakdown
        long transfers = transactionRepository.findAll()
                .stream()
                .filter(t -> t.getType()
                        == TransactionType.TRANSFER)
                .count();
        long deposits = transactionRepository.findAll()
                .stream()
                .filter(t -> t.getType()
                        == TransactionType.DEPOSIT)
                .count();
        long withdrawals =
                transactionRepository.findAll()
                        .stream()
                        .filter(t -> t.getType()
                                == TransactionType
                                        .WITHDRAWAL)
                        .count();
        long loanCredits =
                transactionRepository.findAll()
                        .stream()
                        .filter(t -> t.getType()
                                == TransactionType
                                        .LOAN_CREDIT)
                        .count();

        model.addAttribute("transfers", transfers);
        model.addAttribute("deposits", deposits);
        model.addAttribute("withdrawals", withdrawals);
        model.addAttribute("loanCredits", loanCredits);

        // Total transaction volume
        BigDecimal totalVolume =
                transactionRepository.findAll()
                        .stream()
                        .filter(t -> t.getStatus()
                                == TransactionStatus
                                        .SUCCESS)
                        .map(t -> t.getAmount())
                        .reduce(BigDecimal.ZERO,
                                BigDecimal::add);
        model.addAttribute("totalVolume",
                totalVolume);

        // Loan stats
        model.addAttribute("pendingLoans",
                loanRepository.findAll().stream()
                        .filter(l -> l.getStatus()
                                .name().equals("PENDING"))
                        .count());
        model.addAttribute("approvedLoans",
                loanRepository.findAll().stream()
                        .filter(l -> l.getStatus()
                                .name().equals("APPROVED"))
                        .count());
        model.addAttribute("rejectedLoans",
                loanRepository.findAll().stream()
                        .filter(l -> l.getStatus()
                                .name().equals("REJECTED"))
                        .count());

        // Total loan amount approved
        BigDecimal totalLoanAmount =
                loanRepository.findAll().stream()
                        .filter(l -> l.getStatus()
                                .name().equals("APPROVED"))
                        .map(l -> l.getAmount())
                        .reduce(BigDecimal.ZERO,
                                BigDecimal::add);
        model.addAttribute("totalLoanAmount",
                totalLoanAmount);

        // FD stats
        model.addAttribute("activeFDs",
                fdRepository.findByActiveTrue().size());
        BigDecimal totalFDAmount =
                fdRepository.findByActiveTrue()
                        .stream()
                        .map(f -> f.getPrincipalAmount())
                        .reduce(BigDecimal.ZERO,
                                BigDecimal::add);
        model.addAttribute("totalFDAmount",
                totalFDAmount);

        // User role distribution
        model.addAttribute("customerCount",
                userRepository.findByRole(
                        Role.CUSTOMER).size());
        model.addAttribute("managerCount",
                userRepository.findByRole(
                        Role.MANAGER).size());
        model.addAttribute("tellerCount",
                userRepository.findByRole(
                        Role.TELLER).size());

        // Total deposits in bank
        BigDecimal bankBalance =
                accountRepository.findAll()
                        .stream()
                        .map(a -> a.getBalance())
                        .reduce(BigDecimal.ZERO,
                                BigDecimal::add);
        model.addAttribute("bankBalance",
                bankBalance);

        // Recent transactions for chart
        model.addAttribute("last10Transactions",
                transactionRepository.findAll()
                        .stream()
                        .sorted((a, b) ->
                                b.getTimestamp()
                                 .compareTo(
                                         a.getTimestamp()))
                        .limit(10).toList());

        return "admin/reports";
    }
}
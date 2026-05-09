package com.securebank.controller.manager;

import com.securebank.model.User;
import com.securebank.service.LoanService;
import com.securebank.service.ManagerService;
import com.securebank.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final ManagerService managerService;
    private final LoanService loanService;
    private final UserService userService;

    public ManagerController(ManagerService managerService,
                             LoanService loanService,
                             UserService userService) {
        this.managerService = managerService;
        this.loanService = loanService;
        this.userService = userService;
    }

    private User getLoggedInUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // ===== DASHBOARD =====

    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        User manager = getLoggedInUser(userDetails);
        model.addAttribute("manager", manager);
        model.addAttribute("pendingCustomers",
                managerService.countPendingCustomers());
        model.addAttribute("totalCustomers",
                managerService.countAllCustomers());
        model.addAttribute("totalTellers",
                managerService.countAllTellers());
        model.addAttribute("pendingLoans",
                loanService.countPendingLoans());
        model.addAttribute("recentPendingCustomers",
                managerService.getPendingCustomers());
        model.addAttribute("recentPendingLoans",
                loanService.getPendingLoans());

        return "manager/dashboard";
    }

    // ===== CUSTOMERS =====

    @GetMapping("/customers")
    public String customers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "all") String filter,
            Model model) {

        model.addAttribute("manager",
                getLoggedInUser(userDetails));
        model.addAttribute("filter", filter);

        if ("pending".equals(filter)) {
            model.addAttribute("customers",
                    managerService.getPendingCustomers());
        } else {
            model.addAttribute("customers",
                    managerService.getAllCustomers());
        }

        model.addAttribute("pendingCount",
                managerService.countPendingCustomers());
        return "manager/customers";
    }

    @GetMapping("/customers/{id}")
    public String customerDetail(@PathVariable Long id,
                                 Model model) {
        User customer = managerService.findCustomerById(id)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        model.addAttribute("customer", customer);
        model.addAttribute("loans",
                loanService.getLoansByUser(customer));
        return "manager/customer-detail";
    }

    @PostMapping("/customers/{id}/approve")
    public String approveCustomer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            managerService.approveCustomer(id);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Customer account approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    e.getMessage());
        }
        return "redirect:/manager/customers?filter=pending";
    }

    @PostMapping("/customers/{id}/deactivate")
    public String deactivateCustomer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            managerService.deactivateCustomer(id);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Customer account deactivated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    e.getMessage());
        }
        return "redirect:/manager/customers";
    }

    // ===== LOANS =====

    @GetMapping("/loans")
    public String loans(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "pending") String filter,
            Model model) {

        model.addAttribute("manager",
                getLoggedInUser(userDetails));
        model.addAttribute("filter", filter);

        if ("all".equals(filter)) {
            model.addAttribute("loans",
                    loanService.getAllLoans());
        } else {
            model.addAttribute("loans",
                    loanService.getPendingLoans());
        }

        model.addAttribute("pendingCount",
                loanService.countPendingLoans());
        return "manager/loans";
    }

    @PostMapping("/loans/{id}/approve")
    public String approveLoan(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Approved") String remarks,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            User manager = getLoggedInUser(userDetails);
            loanService.approveLoan(id, remarks, manager);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Loan approved and amount credited to customer.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    e.getMessage());
        }
        return "redirect:/manager/loans";
    }

    @PostMapping("/loans/{id}/reject")
    public String rejectLoan(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Rejected") String remarks,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            User manager = getLoggedInUser(userDetails);
            loanService.rejectLoan(id, remarks, manager);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Loan application rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    e.getMessage());
        }
        return "redirect:/manager/loans";
    }

    // ===== TELLERS =====

    @GetMapping("/tellers")
    public String tellers(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        model.addAttribute("manager",
                getLoggedInUser(userDetails));
        model.addAttribute("tellers",
                managerService.getAllTellers());
        return "manager/tellers";
    }

    @PostMapping("/tellers/create")
    public String createTeller(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            RedirectAttributes redirectAttributes) {
        try {
            managerService.createTeller(fullName, email, phone);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Teller created! Default password: Teller@123");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    e.getMessage());
        }
        return "redirect:/manager/tellers";
    }
}
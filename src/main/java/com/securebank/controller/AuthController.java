package com.securebank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.securebank.dto.request.RegisterRequest;
import com.securebank.service.UserService;
import com.securebank.util.PasswordMatchValidator;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordMatchValidator passwordMatchValidator;

    public AuthController(UserService userService,
                          PasswordMatchValidator passwordMatchValidator) {
        this.userService = userService;
        this.passwordMatchValidator = passwordMatchValidator;
    }

    // ===== REGISTER VALIDATOR =====

    @InitBinder("registerRequest")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(passwordMatchValidator);
    }

    // ===== LOGIN =====

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String expired,
                            Model model) {

        if (error != null) {
            model.addAttribute(
                    "errorMsg",
                    "Invalid email or password. Account may be locked after 5 attempts."
            );
        }

        if (logout != null) {
            model.addAttribute(
                    "successMsg",
                    "You have been logged out successfully."
            );
        }

        if (expired != null) {
            model.addAttribute(
                    "errorMsg",
                    "Your session has expired. Please login again."
            );
        }

        return "auth/login";
    }

    // ===== REGISTER =====

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute(
                "registerRequest",
                new RegisterRequest()
        );

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerRequest")
            RegisterRequest request,

            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Validation Errors
        if (result.hasErrors()) {
            return "auth/register";
        }

        // Email already exists
        if (userService.existsByEmail(request.getEmail())) {

            model.addAttribute(
                    "errorMsg",
                    "Email is already registered"
            );

            return "auth/register";
        }

        try {

            userService.registerCustomer(request);

            redirectAttributes.addFlashAttribute(
                    "successMsg",
                    "Registration successful! Please wait for account approval."
            );

            return "redirect:/auth/login";

        } catch (Exception e) {

            model.addAttribute(
                    "errorMsg",
                    e.getMessage()
            );

            return "auth/register";
        }
    }

    // ===== FORGOT PASSWORD =====

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {

        try {

            userService.initiatePasswordReset(email);

            redirectAttributes.addFlashAttribute(
                    "successMsg",
                    "OTP sent to your email. Valid for 5 minutes."
            );

            redirectAttributes.addFlashAttribute(
                    "email",
                    email
            );

            return "redirect:/auth/reset-password";

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMsg",
                    e.getMessage()
            );

            return "redirect:/auth/forgot-password";
        }
    }

    // ===== RESET PASSWORD =====

    @GetMapping("/reset-password")
    public String resetPasswordPage(Model model) {

        if (!model.containsAttribute("email")) {
            return "redirect:/auth/forgot-password";
        }

        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword,
            @RequestParam String confirmNewPassword,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmNewPassword)) {

            redirectAttributes.addFlashAttribute(
                    "errorMsg",
                    "Passwords do not match"
            );

            redirectAttributes.addFlashAttribute(
                    "email",
                    email
            );

            return "redirect:/auth/reset-password";
        }

        try {

            userService.resetPassword(
                    email,
                    otp,
                    newPassword
            );

            redirectAttributes.addFlashAttribute(
                    "successMsg",
                    "Password reset successful! Please login."
            );

            return "redirect:/auth/login";

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMsg",
                    e.getMessage()
            );

            redirectAttributes.addFlashAttribute(
                    "email",
                    email
            );

            return "redirect:/auth/reset-password";
        }
    }

    // ===== API DOCS =====

    @GetMapping("/api-docs")
    public String apiDocs() {
        return "api-docs";
    }

    // ===== ACCESS DENIED =====

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}
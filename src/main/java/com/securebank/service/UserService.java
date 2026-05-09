package com.securebank.service;

import com.securebank.dto.request.RegisterRequest;
import com.securebank.model.OtpToken;
import com.securebank.model.User;
import com.securebank.model.enums.Role;
import com.securebank.repository.OtpTokenRepository;
import com.securebank.repository.UserRepository;
import com.securebank.util.OtpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpUtil otpUtil;
    private final EmailService emailService;

    @Value("${app.otp.expiry-minutes}")
    private int otpExpiryMinutes;

    public UserService(
            UserRepository userRepository,
            OtpTokenRepository otpTokenRepository,
            PasswordEncoder passwordEncoder,
            OtpUtil otpUtil,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.otpTokenRepository = otpTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpUtil = otpUtil;
        this.emailService = emailService;
    }

    // ===== REGISTER =====

    @Transactional
    public void registerCustomer(RegisterRequest request) {
        if (userRepository.existsByEmail(
                request.getEmail())) {
            throw new RuntimeException(
                    "Email already registered: "
                            + request.getEmail());
        }
        if (!request.getPassword().equals(
                request.getConfirmPassword())) {
            throw new RuntimeException(
                    "Passwords do not match");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(
                request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setActive(false);
        user.setLocked(false);
        user.setFailedLoginAttempts(0);
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        userRepository.save(user);
        emailService.sendWelcomeEmail(
                user.getEmail(), user.getFullName());
    }

    // ===== SAVE USER =====

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // ===== CHANGE PASSWORD =====

    @Transactional
    public void changePassword(User user,
                               String currentPassword,
                               String newPassword,
                               String confirmPassword) {
        if (!passwordEncoder.matches(
                currentPassword, user.getPassword())) {
            throw new RuntimeException(
                    "Current password is incorrect");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException(
                    "New passwords do not match");
        }
        if (newPassword.length() < 8) {
            throw new RuntimeException(
                    "Password must be at least 8 characters");
        }
        user.setPassword(
                passwordEncoder.encode(newPassword));
        userRepository.save(user);
        emailService.sendPasswordChangedEmail(
        	    user.getEmail(),
        	    user.getFullName(),
        	    "N/A"
        	);
    }

    // ===== FAILED LOGIN =====

    @Transactional
    public void handleFailedLogin(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    int attempts =
                            user.getFailedLoginAttempts() + 1;
                    user.setFailedLoginAttempts(attempts);
                    if (attempts >= MAX_FAILED_ATTEMPTS) {
                        user.setLocked(true);
                    }
                    if (attempts >= MAX_FAILED_ATTEMPTS) {
                        user.setLocked(true);
                        userRepository.save(user);
                        // Send lock notification
                        emailService.sendAccountLockedEmail(
                            user.getEmail(),
                            user.getFullName(),
                            null
                        );
                    }
                    userRepository.save(user);
                });
    }

    @Transactional
    public void resetFailedAttempts(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setFailedLoginAttempts(0);
                    userRepository.save(user);
                });
    }

    // ===== PASSWORD RESET =====

    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "No account found: " + email));

        String otp = otpUtil.generateOtp();

        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setOtp(otp);
        token.setPurpose("PASSWORD_RESET");
        token.setUsed(false);
        token.setExpiresAt(LocalDateTime.now()
                .plusMinutes(otpExpiryMinutes));

        otpTokenRepository.save(token);
        emailService.sendOtpEmail(
                email, user.getFullName(),
                otp, "PASSWORD_RESET");
    }

    @Transactional
    public void resetPassword(String email,
                              String otp,
                              String newPassword) {
        OtpToken token = otpTokenRepository
                .findTopByEmailAndPurposeAndUsedFalseOrderByExpiresAtDesc(
                        email, "PASSWORD_RESET")
                .orElseThrow(() -> new RuntimeException(
                        "Invalid or expired OTP"));

        if (!token.getOtp().equals(otp)) {
            throw new RuntimeException(
                    "Incorrect OTP entered");
        }
        if (token.getExpiresAt()
                .isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }

        token.setUsed(true);
        otpTokenRepository.save(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "User not found"));
        user.setPassword(
                passwordEncoder.encode(newPassword));
        user.setFailedLoginAttempts(0);
        user.setLocked(false);
        userRepository.save(user);
    }

    // ===== HELPERS =====

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
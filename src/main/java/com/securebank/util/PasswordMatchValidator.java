package com.securebank.util;

import com.securebank.dto.request.RegisterRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PasswordMatchValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterRequest request = (RegisterRequest) target;
        if (request.getPassword() != null
                && request.getConfirmPassword() != null
                && !request.getPassword()
                        .equals(request.getConfirmPassword())) {
            errors.rejectValue(
                    "confirmPassword",
                    "password.mismatch",
                    "Passwords do not match");
        }
    }
}
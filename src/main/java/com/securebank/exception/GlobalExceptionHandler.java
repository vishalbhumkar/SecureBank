package com.securebank.exception;

import com.securebank.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access
        .AccessDeniedException;
import org.springframework.security.core
        .AuthenticationException;
import org.springframework.validation
        .FieldError;
import org.springframework.web.bind.annotation
        .ExceptionHandler;
import org.springframework.web.bind.annotation
        .RestControllerAdvice;
import org.springframework.web.bind
        .MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(
        basePackages = "com.securebank.api")
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>>
            handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        ex.getMessage(), 400));
    }

    @ExceptionHandler(
            AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>>
            handleAccessDenied(
                    AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(
                        "Access denied", 403));
    }

    @ExceptionHandler(
            AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>>
            handleAuth(
                    AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        "Unauthorized", 401));
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String,String>>>
            handleValidation(
                    MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
          .getAllErrors()
          .forEach(error -> {
              String field = ((FieldError) error)
                      .getField();
              String msg = error.getDefaultMessage();
              errors.put(field, msg);
          });

        ApiResponse<Map<String, String>> response =
                new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(
                "Validation failed");
        response.setData(errors);
        response.setStatus(400);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>>
            handleGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus
                        .INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "Internal server error: "
                                + ex.getMessage(),
                        500));
    }
}
package com.microservices.banking.account.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.microservices.banking.account.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 🔴 Not Found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(NotFoundException ex) {

        log.error("NotFoundException: {}", ex.getMessage());

        return new ResponseEntity<>(
                ApiResponse.builder()
                        .success(false)
                        .message("Resource not found")
                        .error(ex.getMessage())
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }

    // 🔴 Insufficient Balance
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<?>> handleBalance(InsufficientBalanceException ex) {

        log.error("InsufficientBalanceException: {}", ex.getMessage());

        return new ResponseEntity<>(
                ApiResponse.builder()
                        .success(false)
                        .message("Transaction failed")
                        .error(ex.getMessage())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    // 🔴 Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneric(Exception ex) {

        log.error("Unhandled Exception: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(
                ApiResponse.builder()
                        .success(false)
                        .message("Something went wrong")
                        .error(ex.getMessage())
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
package com.bigJoe.country_currency_exchange_rate.controller;

import com.bigJoe.country_currency_exchange_rate.exceptions.ApiUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleApiError(ApiUnavailableException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ex.toResponse());
    }
}


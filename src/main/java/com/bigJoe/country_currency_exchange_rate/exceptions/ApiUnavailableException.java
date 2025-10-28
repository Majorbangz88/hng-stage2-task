package com.bigJoe.country_currency_exchange_rate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ApiUnavailableException extends RuntimeException {
    private final String error;
    private final String details;

    public ApiUnavailableException(String error, String details) {
        super(error + " - " + details);
        this.error = error;
        this.details = details;
    }

    public Map<String, String> toResponse() {
        return Map.of("error", error, "details", details);
    }
}


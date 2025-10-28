package com.bigJoe.country_currency_exchange_rate.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDataException extends RuntimeException {
    private final Map<String, String> details;

    public InvalidDataException(Map<String, String> details) {
        super("Validation failed");
        this.details = details;
    }

}


package com.bigJoe.country_currency_exchange_rate.utils;

import com.bigJoe.country_currency_exchange_rate.exceptions.ApiUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

@Slf4j
@Component
public class CountryUtil {

    @Value("${COUNTRY_DATA_URL}")
    private String countryDataUrl;

    @Value("${EXCHANGE_RATE_URL}")
    private String exchangeRateUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Bean
    public RestTemplate restTemplate() {
        System.setProperty("https.protocols", "TLSv1.2");
        return new RestTemplate();
    }


    public List<Map<String, Object>> getCountryData() {
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(countryDataUrl, List.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ApiUnavailableException(
                        "External data source unavailable",
                        "Could not fetch data from " + countryDataUrl
                );
            }

            List<Map<String, Object>> body = response.getBody();

            if (body == null || body.isEmpty()) {
                throw new ApiUnavailableException(
                        "Empty response received",
                        "The country API returned no data: " + countryDataUrl
                );
            }

            return body;

        } catch (Exception e) {
            throw new ApiUnavailableException(
                    "Failed to fetch external data",
                    "Cause: " + e.getMessage() + " | URL: " + countryDataUrl
            );
        }
    }

    public Map<String, Double> getExchangeRate() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(exchangeRateUrl, Map.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ApiUnavailableException(
                        "External data source unavailable",
                        "Could not fetch data from " + countryDataUrl
                );
            }

            Map<String, Object> data = response.getBody();

            if (data == null || data.isEmpty()) {
                throw new ApiUnavailableException(
                        "Empty response received",
                        "The country API returned no data: " + exchangeRateUrl
                );
            }

            if (data.containsKey("rates")) {
                return (Map<String, Double>) data.get("rates");
            }
            return Collections.emptyMap();
        } catch (Exception e) {
            throw new ApiUnavailableException(
                    "Failed to fetch external data",
                    "Cause: " + e.getMessage() + " | URL: " + exchangeRateUrl
            );
        }
    }

    public String getCapital(Map<String, Object> country) {
        try {
            List<String> capitals = (List<String>) country.get("capital");
            return capitals != null && !capitals.isEmpty() ? capitals.get(0) : "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }

    public String getFlag(Map<String, Object> country) {
        try {
            Map<String, Object> flags = (Map<String, Object>) country.get("flags");
            return flags != null ? (String) flags.get("png") : "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }
}

package com.bigJoe.country_currency_exchange_rate.controller;

import com.bigJoe.country_currency_exchange_rate.data.model.Country;
import com.bigJoe.country_currency_exchange_rate.service.CountryImageService;
import com.bigJoe.country_currency_exchange_rate.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @Autowired
    private CountryImageService imageService;

    @PostMapping("/countries/refresh")
    public ResponseEntity<List<Country>> refreshCountry() {
        List<Country> refreshedCountries = countryService.buildCountryList();
        imageService.generateSummaryImage();
        return ResponseEntity.ok(refreshedCountries);
    }

    @GetMapping("/countries")
    public ResponseEntity<List<Country>> getFilteredCountries(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String sort) {

        List<Country> countries = countryService.getFilteredCountries(region, currency, sort);
        return ResponseEntity.ok(countries);
    }

    @GetMapping("/countries/{name}")
    public ResponseEntity<Country> getSpecificCountry(@PathVariable("name") String name) {
        Country country = countryService.getCountry(name);
        return ResponseEntity.ok(country);
    }

    @DeleteMapping("/countries/{name}")
    public ResponseEntity<Void> deleteCountry(@PathVariable("name") String name) {
        countryService.deleteCountry(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/countries/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> status = countryService.getStatus();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/countries/image")
    public ResponseEntity<?> getSummaryImage() {
        File image = imageService.getSummaryImage();

        if (image == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Summary image not found"));
        }

        try {
            byte[] imageBytes = java.nio.file.Files.readAllBytes(image.toPath());
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.IMAGE_PNG)
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not read summary image"));
        }
    }
}

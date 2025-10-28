package com.bigJoe.country_currency_exchange_rate.service;

import com.bigJoe.country_currency_exchange_rate.data.model.Country;
import com.bigJoe.country_currency_exchange_rate.data.repository.CountryRepository;
import com.bigJoe.country_currency_exchange_rate.exceptions.CountryNotFoundException;
import com.bigJoe.country_currency_exchange_rate.utils.CountryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CountryServiceImpl implements CountryService {

    @Autowired
    private CountryUtil countryUtil;
    @Autowired
    private CountryRepository repository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CountryUtil.class);

//    public

    public List<Country> buildCountryList() {
        List<Map<String, Object>> countries = countryUtil.getCountryData();
        Map<String, Object> rates = (Map<String, Object>) (Map<?, ?>) countryUtil.getExchangeRate(); // safer cast

        LOGGER.info(countries.get(0).toString());
        LOGGER.info(rates.toString());

        List<Country> countryList = countries.stream()
                .map(country -> {
                    String currencyCode = null;
                    String exchangeRate = null;
                    Double estimatedGdp = 0.0;
                    int population = ((Number) country.getOrDefault("population", 0)).intValue();

                    try {
                        List<Map<String, Object>> currencies = (List<Map<String, Object>>) country.get("currencies");

                        if (currencies == null || currencies.isEmpty()) {
                            estimatedGdp = 0.0;
                        } else {
                            Map<String, Object> firstCurrency = currencies.get(0);
                            currencyCode = (String) firstCurrency.get("code");

                            if (currencyCode != null && rates.containsKey(currencyCode)) {
                                Object rateObj = rates.get(currencyCode);

                                if (rateObj instanceof Number) {
                                    exchangeRate = String.valueOf(((Number) rateObj).doubleValue());
                                }

                                if (exchangeRate != null && Double.parseDouble(exchangeRate) > 0) {
                                    double randomMultiplier = 1000 + Math.random() * 1000;
                                    estimatedGdp = (population * randomMultiplier) / Double.parseDouble(exchangeRate);
                                } else {
                                    exchangeRate = null;
                                    estimatedGdp = 0.0;
                                }
                            } else {
                                estimatedGdp = 0.0;
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.warn("Error processing country: {}", country.get("name"), e);
                    }

                    return Country.builder()
                            .name((String) country.get("name"))
                            .capital((String) country.get("capital"))
                            .region((String) country.get("region"))
                            .population(population)
                            .currency_code(currencyCode)
                            .exchange_rate(exchangeRate)
                            .estimated_gdp(estimatedGdp)
                            .flag_url((String) country.get("flag"))
                            .lastRefreshedAt(LocalDateTime.now())
                            .build();
                })
                .collect(Collectors.toList());

        repository.saveAll(countryList);
        return countryList;
    }

    public List<Country> getFilteredCountries(String region, String currency, String sort) {
        List<Country> countries = repository.findAll();

        if (region != null && !region.isEmpty()) {
            countries = countries.stream()
                    .filter(country -> country.getRegion().equalsIgnoreCase(region))
                    .toList();
        }

        if (currency != null && !currency.isEmpty()) {
            countries = countries.stream()
                    .filter(country -> country.getCurrency_code().equalsIgnoreCase(currency))
                    .toList();
        }

        if (sort != null && !sort.isEmpty()) {
            switch (sort.toLowerCase()) {
                case "gdp_desc" -> countries = countries.stream()
                        .sorted(Comparator.comparingDouble(Country::getEstimated_gdp).reversed())
                        .toList();
                case "gdp_asc" -> countries = countries.stream()
                        .sorted(Comparator.comparingDouble(Country::getEstimated_gdp))
                        .toList();
                case "name_asc" -> countries = countries.stream()
                        .sorted(Comparator.comparing(Country::getName))
                        .toList();
                case "name_desc" -> countries = countries.stream()
                        .sorted(Comparator.comparing(Country::getName).reversed())
                        .toList();
                default -> {

                }
            }
        }

        return countries;
    }

    public Country getCountry(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new CountryNotFoundException("Country not found"));
    }

    @Override
    public void deleteCountry(String name) {
        Country country = repository.findByName(name)
                .orElseThrow(() -> new CountryNotFoundException("Country not found"));

        repository.delete(country);
    }

    @Override
    public Map<String, Object> getStatus() {
        long totalCountries = repository.count();
        LocalDateTime lastRefresh = repository.findTopByOrderByLastRefreshedAtDesc()
                .map(Country::getLastRefreshedAt)
                .orElse(null);

        Map<String, Object> status = new HashMap<>();
        status.put("total_countries", totalCountries);
        status.put("last_Refreshed_at", lastRefresh);

        return status;
    }


}

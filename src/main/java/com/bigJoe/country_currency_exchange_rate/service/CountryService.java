package com.bigJoe.country_currency_exchange_rate.service;

import com.bigJoe.country_currency_exchange_rate.data.model.Country;

import java.util.List;
import java.util.Map;

public interface CountryService {

    List<Country> buildCountryList();

    List<Country> getFilteredCountries(String region, String currency, String sort);

    Country getCountry(String name);

    void deleteCountry(String name);

    Map<String, Object> getStatus();

    Country updateOrInsertCountry(String name, String population, String currencyCode);
}

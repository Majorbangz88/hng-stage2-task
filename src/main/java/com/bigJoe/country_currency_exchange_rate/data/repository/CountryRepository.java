package com.bigJoe.country_currency_exchange_rate.data.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.bigJoe.country_currency_exchange_rate.data.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByName(String name);

    Optional<Country> findTopByOrderByLastRefreshedAtDesc();
}


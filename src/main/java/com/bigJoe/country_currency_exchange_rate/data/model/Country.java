package com.bigJoe.country_currency_exchange_rate.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "country")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String capital;
    private String region;
    private int population;
    private String currency_code;
    private String exchange_rate;
    private double estimated_gdp;
    private String flag_url;
    @Column(name = "last_refreshed_at")
    @JsonProperty("last_refresh_date")
    private LocalDateTime lastRefreshedAt;
}


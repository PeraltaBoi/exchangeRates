package com.currencyexchange.ExchangeRateApi.services.interfaces;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.currencyexchange.ExchangeRateApi.domain.ExchangeRates;

public interface IRateService {
    /**
     * Get exchange rate between two currencies
     */
    Optional<BigDecimal> getExchangeRate(String sourceCurrency, String targetCurrency);

    /**
     * Get all exchange rates for a source currency
     */
    Optional<ExchangeRates> getAllExchangeRates(String sourceCurrency);

    /**
     * Convert amount from source currency to target currency
     */
    Optional<BigDecimal> convertAmount(BigDecimal amount, String sourceCurrency, String targetCurrency);

    /**
     * Convert amount from source currency to multiple target currencies
     */
    Map<String, Optional<BigDecimal>> convertAmountToMultipleCurrencies(BigDecimal amount, String sourceCurrency, List<String> targetCurrencies);
}

package com.currencyexchange.ExchangeRateApi.services.interfaces;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.currencyexchange.ExchangeRateApi.domain.ExchangeRates;

public interface IRateService {
  /**
   * Get exchange rate between two currencies
   */
  BigDecimal getExchangeRate(String sourceCurrency, String targetCurrency);

  /**
   * Get all exchange rates for a source currency
   */
  ExchangeRates getAllExchangeRates(String sourceCurrency);

  /**
   * Convert amount from source currency to one or more target currencies
   */
  Map<String, BigDecimal> convertAmount(BigDecimal amount, String sourceCurrency, List<String> targetCurrencies);
}

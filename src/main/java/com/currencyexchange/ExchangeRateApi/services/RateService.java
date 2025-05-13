package com.currencyexchange.ExchangeRateApi.services;

import com.currencyexchange.ExchangeRateApi.domain.CurrencyPair;
import com.currencyexchange.ExchangeRateApi.domain.ExchangeRates;
import com.currencyexchange.ExchangeRateApi.exceptions.ExchangeRateNotFoundException;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IExchangeRateProviderService;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IRateService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RateService implements IRateService {
  private final IExchangeRateProviderService exchangeRateProvider;
  /**
   * Get exchange rate between two currencies
   */
  public BigDecimal getExchangeRate(String sourceCurrency, String targetCurrency) {
    return exchangeRateProvider.getExchangeRates()
        .flatMap(rates -> getExchangeRate(rates, sourceCurrency, targetCurrency))
        .orElseThrow(() -> new ExchangeRateNotFoundException(sourceCurrency, targetCurrency));
  }

  /**
   * Get all exchange rates for a source currency
   */
  public ExchangeRates getAllExchangeRates(String sourceCurrency) {
    return exchangeRateProvider.getExchangeRates()
        .flatMap(rates -> filterExchangeRates(rates, sourceCurrency))
        .orElseThrow(() -> new ExchangeRateNotFoundException(sourceCurrency));
  }

  /**
   * Convert amount from source currency to multiple target currencies
   */
  public Map<String, BigDecimal> convertAmount(BigDecimal amount, String sourceCurrency,
      List<String> targetCurrencies) {
    return targetCurrencies.stream()
        .collect(Collectors.toMap(
            targetCurrency -> targetCurrency,
            targetCurrency -> convertAmountToCurrency(amount, sourceCurrency, targetCurrency)));
  }

  /**
   * Filters exchange rates for a specific source currency.
   * 
   * @param rates The original exchange rates containing all currency pairs
   * @param from  The source currency code to filter by (e.g., "EUR", "USD")
   * @return A new ExchangeRates object containing only rates for the specified
   *         source currency
   */
  private Optional<ExchangeRates> filterExchangeRates(@NonNull ExchangeRates rates, @NonNull String from) {
    Map<CurrencyPair, BigDecimal> filteredQuotes = rates.getQuotes().entrySet().stream()
        .filter(entry -> from.equals(entry.getKey().getFrom()))
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue));

    if (filteredQuotes.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new ExchangeRates(filteredQuotes));
  }

  /**
   * Gets the exchange rate for a specific currency pair.
   * 
   * @param rates The exchange rates containing all currency pairs
   * @param from  The source currency code (e.g., "EUR", "USD")
   * @param to    The target currency code (e.g., "EUR", "USD")
   * @return Optional containing the exchange rate if found, empty Optional
   *         otherwise
   */
  private Optional<BigDecimal> getExchangeRate(@NonNull ExchangeRates rates,
      @NonNull String from,
      @NonNull String to) {
    return rates.getQuotes().entrySet().stream()
        .filter(entry -> from.equals(entry.getKey().getFrom())
            && to.equals(entry.getKey().getTo()))
        .map(Map.Entry::getValue)
        .findFirst();
  }

  /**
   * Convert amount from source currency to target currency
   * 
   * @param amount         The amount to be converted
   * @param sourceCurrency The source currency code (e.g., "EUR", "USD")
   * @param targetCurrency The target currency code (e.g., "EUR", "USD")
   * @return The converted amount
   */
  private BigDecimal convertAmountToCurrency(BigDecimal amount, String sourceCurrency, String targetCurrency) {
    BigDecimal exchangeRate = getExchangeRate(sourceCurrency, targetCurrency);
    return amount.multiply(exchangeRate);
  }
}

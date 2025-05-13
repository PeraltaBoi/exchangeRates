package com.currencyexchange.ExchangeRateApi.services;

import com.currencyexchange.ExchangeRateApi.domain.CurrencyPair;
import com.currencyexchange.ExchangeRateApi.domain.ExchangeRates;
import com.currencyexchange.ExchangeRateApi.domain.ExchangeRatesFromBase;
import com.currencyexchange.ExchangeRateApi.infrastructure.exchanges.IExchangeRateProvider;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IExchangeRateProviderService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Primary
public class ExchangeRateProviderService implements IExchangeRateProviderService {
  private final IExchangeRateProvider exchangeRateProvider;
  private final MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
  private static final String CACHE_KEY = "exchange_rates";

  /**
   * Retrieves all exchange rates from the provider and calculates cross rates.
   * Returns a complete set of exchange rates including direct, inverse, and cross
   * rates.
   * 
   * @return An Optional containing {@link ExchangeRates} with all possible
   *         currency pairs
   *         and their rates, or empty Optional if no rates are available from the
   *         provider.
   * @see ExchangeRates
   * @see ExchangeRateProvider#getAllRates()
   */
  @Override
  @Cacheable(value = CACHE_KEY, unless = "#result == null")
  public Optional<ExchangeRates> getExchangeRates() {
    return exchangeRateProvider.getAllRates()
        .map(this::createExchangePairs);
  }

  /**
   * Creates a complete map of exchange rate pairs including direct, inverse, and
   * cross rates.
   * Currency pairs are formatted as FROMTO (e.g., USDEUR, EURUSD)
   *
   * @param baseRates The exchange rates response containing base currency and
   *                  rates
   * @return ExchangeRates containing all possible currency pairs and their rates
   * @throws IllegalArgumentException if response contains invalid or empty data
   */
  @NonNull
  private ExchangeRates createExchangePairs(@NonNull ExchangeRatesFromBase baseRates) {
    Map<CurrencyPair, BigDecimal> sourceRates = baseRates.getQuotes();
    if (sourceRates.isEmpty()) {
      throw new IllegalArgumentException("Exchange rates cannot be empty");
    }

    Map<CurrencyPair, BigDecimal> pairs = new HashMap<>();
    String sourceCurrency = baseRates.getSource();

    sourceRates.forEach((key, rate) -> {
      String targetCurrency = key.getTo();

      // Add direct rate
      pairs.put(new CurrencyPair(sourceCurrency, targetCurrency), rate);

      // Add inverse rate
      pairs.put(new CurrencyPair(targetCurrency, sourceCurrency), BigDecimal.ONE.divide(rate, mc));

      // Calculate cross rates between all currencies except the base
      sourceRates.forEach((key2, rate2) -> {
        String targetCurrency2 = key2.getTo();
        if (!targetCurrency.equals(targetCurrency2)) {
          // Cross rate: rate2/rate1 (e.g., GBPJPY = EURGBP/EURJPY)
          pairs.put(new CurrencyPair(targetCurrency, targetCurrency2), rate2.divide(rate, mc));
        }
      });
    });

    return new ExchangeRates(pairs);
  }

}

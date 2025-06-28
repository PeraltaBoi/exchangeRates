package com.currencyexchange.ExchangeRateApi.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import com.currencyexchange.ExchangeRateApi.domain.CurrencyPair;
import com.currencyexchange.ExchangeRateApi.domain.ExchangeRates;
import com.currencyexchange.ExchangeRateApi.domain.ExchangeRatesFromBase;
import com.currencyexchange.ExchangeRateApi.infrastructure.exchanges.IExchangeRateProvider;

@ExtendWith(MockitoExtension.class)
class ExchangeRateProviderServiceTest {

  @Mock
  private IExchangeRateProvider exchangeRateProvider;

  @InjectMocks
  private ExchangeRateProviderService service;

  @Autowired
  private CacheManager cacheManager;

  @BeforeEach
  void setUp() {
    cacheManager = new ConcurrentMapCacheManager("exchange_rates");
    // Clear cache before each test
    cacheManager.getCache("exchange_rates").clear();
  }

  @Test
  void whenGetExchangeRates_thenCalculatesAllPairs() {
    // Arrange
    String baseCurrency = "EUR";
    Map<CurrencyPair, BigDecimal> sourceRates = new HashMap<>();
    sourceRates.put(new CurrencyPair("EUR", "USD"), new BigDecimal("1.10"));
    sourceRates.put(new CurrencyPair("EUR", "GBP"), new BigDecimal("0.85"));

    ExchangeRatesFromBase baseRates = new ExchangeRatesFromBase(baseCurrency, sourceRates);
    when(exchangeRateProvider.getAllRates()).thenReturn(Optional.of(baseRates));

    // Act
    Optional<ExchangeRates> result = service.getExchangeRates();

    // Assert
    assertThat(result).isPresent();
    ExchangeRates rates = result.get();

    // Direct rates
    assertThat(rates.getQuotes().get(new CurrencyPair("EUR", "USD"))).isEqualByComparingTo(new BigDecimal("1.10"));
    assertThat(rates.getQuotes().get(new CurrencyPair("EUR", "GBP"))).isEqualByComparingTo(new BigDecimal("0.85"));

    // Inverse rates
    assertThat(rates.getQuotes().get(new CurrencyPair("USD", "EUR")))
        .isEqualByComparingTo(new BigDecimal("0.9090909091")); // 1/1.10
    assertThat(rates.getQuotes().get(new CurrencyPair("GBP", "EUR")))
        .isEqualByComparingTo(new BigDecimal("1.176470588")); // 1/0.85

    // Cross rates
    assertThat(rates.getQuotes().get(new CurrencyPair("GBP", "USD")))
        .isEqualByComparingTo(new BigDecimal("1.294117647")); // 1.10/0.85
    assertThat(rates.getQuotes().get(new CurrencyPair("USD", "GBP")))
        .isEqualByComparingTo(new BigDecimal("0.7727272727")); // 0.85/1.10
  }

  @Test
  void whenProviderReturnsEmpty_thenReturnEmpty() {
    // Arrange
    when(exchangeRateProvider.getAllRates()).thenReturn(Optional.empty());

    // Act
    Optional<ExchangeRates> result = service.getExchangeRates();

    // Assert
    assertThat(result).isEmpty();
  }

  @Test
  void whenProviderReturnsEmptyRates_thenThrowException() {
    // Arrange
    String baseCurrency = "EUR";
    Map<CurrencyPair, BigDecimal> emptyRates = new HashMap<>();
    ExchangeRatesFromBase baseRates = new ExchangeRatesFromBase(baseCurrency, emptyRates);
    when(exchangeRateProvider.getAllRates()).thenReturn(Optional.of(baseRates));

    // Act & Assert
    assertThatThrownBy(() -> service.getExchangeRates())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Exchange rates cannot be empty");
  }
}

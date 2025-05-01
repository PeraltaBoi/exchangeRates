package com.currencyexchange.ExchangeRateApi.services;

import com.currencyexchange.ExchangeRateApi.domain.ExchangeRatesFromBase;
import com.currencyexchange.ExchangeRateApi.infrastructure.exchanges.IExchangeRateProvider;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IExchangeRateProviderService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Primary
public class ExchangeRateProviderService implements IExchangeRateProviderService {
  private final IExchangeRateProvider exchangeRateProvider;

  /**
   * Retrieves all exchange rates from the provider.
   * All rates are from the same (provider default) currency to some other.
   * 
   * @return An Optional containing {@link ExchangeRatesFromBase} with the source
   *         currency and its quotes,
   *         or empty Optional if no rates are available from the provider.
   * @see ExchangeRatesFromBase
   * @see ExchangeRateProvider#getAllRates()
   */
  public Optional<ExchangeRatesFromBase> getExchangeRates() {
    return exchangeRateProvider.getAllRates()
        .map(rates -> new ExchangeRatesFromBase(rates.getSource(), rates.getQuotes()));
  }
}

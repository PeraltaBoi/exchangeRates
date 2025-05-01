package com.currencyexchange.ExchangeRateApi.services.interfaces;

import com.currencyexchange.ExchangeRateApi.domain.ExchangeRatesFromBase;
import java.util.Optional;

public interface IExchangeRateProviderService {
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
  public Optional<ExchangeRatesFromBase> getExchangeRates();
}

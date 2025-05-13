package com.currencyexchange.ExchangeRateApi.services.interfaces;

import com.currencyexchange.ExchangeRateApi.domain.ExchangeRates;
import java.util.Optional;

public interface IExchangeRateProviderService {
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
  public Optional<ExchangeRates> getExchangeRates();
}

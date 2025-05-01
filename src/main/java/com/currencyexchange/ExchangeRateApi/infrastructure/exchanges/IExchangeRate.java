package com.currencyexchange.ExchangeRateApi.infrastructure.exchanges;

import com.currencyexchange.ExchangeRateApi.domain.ExchangeRatesFromBase;

import java.util.Optional;

public interface IExchangeRate {
	/**
	 * Get all available exchange rates for a given source currency
	 *
	 * @param sourceCurrency the base currency code (e.g., "USD")
	 * @return Optional containing exchange rates from base with all available rates
	 *         if successful, empty if failed
	 */
	Optional<ExchangeRatesFromBase> getAllRates(String sourceCurrency);

	/**
	 * Get the name of the exchange rate provider
	 *
	 * @return the provider name (e.g., "ExchangeRate.host")
	 */
	String getProviderName();
}

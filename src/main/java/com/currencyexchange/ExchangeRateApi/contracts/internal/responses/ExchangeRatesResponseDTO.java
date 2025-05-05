package com.currencyexchange.ExchangeRateApi.contracts.internal.responses;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

import com.currencyexchange.ExchangeRateApi.domain.ExchangeRates;

public record ExchangeRatesResponseDTO(
		String from,
		Map<String, BigDecimal> rates) {

	public ExchangeRatesResponseDTO(String from, ExchangeRates exchangeRates) {
		this(from, exchangeRates.getQuotes().entrySet().stream().collect(Collectors.toMap(
				entry -> entry.getKey().getTo(),
				Map.Entry::getValue)));
	}
}

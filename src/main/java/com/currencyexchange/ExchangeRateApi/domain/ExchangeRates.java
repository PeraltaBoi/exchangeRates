package com.currencyexchange.ExchangeRateApi.domain;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeRates {
	private Map<CurrencyPair, BigDecimal> quotes;
}

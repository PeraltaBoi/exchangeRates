package com.currencyexchange.ExchangeRateApi.domain;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRates {
	private Map<CurrencyPair, BigDecimal> quotes;
}

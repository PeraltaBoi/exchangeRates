package com.currencyexchange.ExchangeRateApi.domain;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class ExchangeRatesFromBase {
	private final String source;
	private final Map<CurrencyPair, BigDecimal> quotes;
}

package com.currencyexchange.ExchangeRateApi.domain;

import java.util.Map;
import lombok.Data;

@Data
public class ExchangeRatesFromBase {
	private final String source;
	private final Map<String, Double> quotes;
}

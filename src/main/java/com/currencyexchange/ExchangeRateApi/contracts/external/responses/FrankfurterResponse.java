package com.currencyexchange.ExchangeRateApi.infrastructure.exchanges.frankfurter;

import java.util.Map;
import lombok.Data;

@Data
public class FrankfurterResponse {
	private String base;
	private String date;
	private Map<String, Double> rates;
}

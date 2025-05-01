package com.currencyexchange.ExchangeRateApi.contracts.external.responses;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class FrankfurterResponse {
	private String base;
	private String date;
	private Map<String, BigDecimal> rates;
}

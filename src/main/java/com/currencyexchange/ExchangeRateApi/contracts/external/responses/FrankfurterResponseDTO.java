package com.currencyexchange.ExchangeRateApi.contracts.external.responses;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class FrankfurterResponseDTO {
	private String base;
	private String date;
	private Map<String, BigDecimal> rates;
}

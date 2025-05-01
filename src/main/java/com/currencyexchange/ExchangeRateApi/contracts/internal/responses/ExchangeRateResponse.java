package com.currencyexchange.ExchangeRateApi.contracts.internal.responses;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponse {
	private String from;
	private Map<String, BigDecimal> rates;
}

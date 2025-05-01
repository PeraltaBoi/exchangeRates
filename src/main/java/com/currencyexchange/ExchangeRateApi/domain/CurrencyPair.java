package com.currencyexchange.ExchangeRateApi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class CurrencyPair {
	@NonNull
	private String from;
	@NonNull
	private String to;
}

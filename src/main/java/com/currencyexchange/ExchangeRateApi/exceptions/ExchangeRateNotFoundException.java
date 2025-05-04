package com.currencyexchange.ExchangeRateApi.exceptions;

import org.springframework.http.HttpStatus;

public class ExchangeRateNotFoundException extends RuntimeException {

	public ExchangeRateNotFoundException(String sourceCurrency) {
		super(String.format("No exchange rates found for %s", sourceCurrency));
	}

	public ExchangeRateNotFoundException(String sourceCurrency, String targetCurrency) {
		super(String.format("No exchange rate found from %s to %s", sourceCurrency, targetCurrency));
	}

	public HttpStatus getStatusCode() {
		return HttpStatus.BAD_REQUEST;
	}
}

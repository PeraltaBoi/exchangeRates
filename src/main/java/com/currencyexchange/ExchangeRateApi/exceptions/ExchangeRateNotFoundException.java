package com.currencyexchange.ExchangeRateApi.exceptions;

import org.springframework.http.HttpStatus;

import com.currencyexchange.ExchangeRateApi.exceptions.interfaces.ICustomStatusException;

public class ExchangeRateNotFoundException extends RuntimeException implements ICustomStatusException {

	public ExchangeRateNotFoundException(String sourceCurrency) {
		super(String.format("No exchange rates found for %s", sourceCurrency));
	}

	public ExchangeRateNotFoundException(String sourceCurrency, String targetCurrency) {
		super(String.format("No exchange rate found from %s to %s", sourceCurrency, targetCurrency));
	}

	@Override
	public HttpStatus getStatusCode() {
		return HttpStatus.BAD_REQUEST;
	}
}

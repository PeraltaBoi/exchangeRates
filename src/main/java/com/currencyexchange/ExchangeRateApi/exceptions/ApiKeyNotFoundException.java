package com.currencyexchange.ExchangeRateApi.exceptions;

import org.springframework.http.HttpStatus;

import com.currencyexchange.ExchangeRateApi.exceptions.interfaces.ICustomStatusException;

public class ApiKeyNotFoundException extends RuntimeException implements ICustomStatusException {

	public ApiKeyNotFoundException(String message) {
		super(message);
	}

	@Override
	public HttpStatus getStatusCode() {
		return HttpStatus.UNAUTHORIZED;
	}
}

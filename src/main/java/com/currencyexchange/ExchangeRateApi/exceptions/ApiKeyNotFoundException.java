package com.currencyexchange.ExchangeRateApi.exceptions;

import org.springframework.http.HttpStatus;

public class ApiKeyNotFoundException extends RuntimeException {

	public ApiKeyNotFoundException(String message) {
		super(message);
	}

	public HttpStatus getStatusCode() {
		return HttpStatus.UNAUTHORIZED;
	}
}

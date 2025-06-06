package com.currencyexchange.ExchangeRateApi.exceptions;

import org.springframework.http.HttpStatus;

public class ApiKeyNotFoundException extends RuntimeException {

	public ApiKeyNotFoundException() {
		super(String.format("No such api key on user's account"));
	}

	public HttpStatus getStatusCode() {
		return HttpStatus.NOT_FOUND;
	}
}

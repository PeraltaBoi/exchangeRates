package com.currencyexchange.ExchangeRateApi.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RuntimeException {

	public UnauthorizedException() {
		super(String.format("Invalid user credentials"));
	}

	public HttpStatus getStatusCode() {
		return HttpStatus.UNAUTHORIZED;
	}
}

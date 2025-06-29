package com.currencyexchange.ExchangeRateApi.exceptions;

import org.springframework.http.HttpStatus;

import com.currencyexchange.ExchangeRateApi.exceptions.interfaces.ICustomStatusException;

public class UnauthorizedException extends RuntimeException implements ICustomStatusException {

	public UnauthorizedException() {
		super(String.format("Invalid user credentials"));
	}

	@Override
	public HttpStatus getStatusCode() {
		return HttpStatus.UNAUTHORIZED;
	}
}

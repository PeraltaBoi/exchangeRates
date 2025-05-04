package com.currencyexchange.ExchangeRateApi.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ExchangeRateNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleExchangeRateNotFoundException(ExchangeRateNotFoundException ex) {
		ErrorResponse error = new ErrorResponse(ex.getMessage());
		return new ResponseEntity<>(error, ex.getStatusCode());
	}

	private record ErrorResponse(String message) {
	}
}

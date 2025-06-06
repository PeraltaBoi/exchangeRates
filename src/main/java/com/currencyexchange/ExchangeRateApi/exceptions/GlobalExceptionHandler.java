package com.currencyexchange.ExchangeRateApi.exceptions;

import com.currencyexchange.ExchangeRateApi.contracts.internal.rest.responses.ErrorResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ExchangeRateNotFoundException.class)
	public ResponseEntity<ErrorResponseDTO> handleExchangeRateNotFoundException(ExchangeRateNotFoundException ex) {
		ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage());
		return new ResponseEntity<>(error, ex.getStatusCode());
	}

	@ExceptionHandler(ApiKeyNotFoundException.class)
	public ResponseEntity<ErrorResponseDTO> handleApiKeyNotFoundException(ApiKeyNotFoundException ex) {
		ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage());
		return new ResponseEntity<>(error, ex.getStatusCode());
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponseDTO> handleUnauthorizedException(UnauthorizedException ex) {
		ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage());
		return new ResponseEntity<>(error, ex.getStatusCode());
	}
}

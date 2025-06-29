package com.currencyexchange.ExchangeRateApi.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.currencyexchange.ExchangeRateApi.contracts.internal.rest.responses.ErrorResponseDTO;
import com.currencyexchange.ExchangeRateApi.exceptions.interfaces.ICustomStatusException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler({
			UsernameAlreadyExistsException.class,
			ExchangeRateNotFoundException.class,
			UnauthorizedException.class,
			ApiKeyNotFoundException.class
	})
	public ResponseEntity<ErrorResponseDTO> handleCustomExceptions(ICustomStatusException ex) {
		logger.warn("A custom exception was handled: {}", ex.getMessage());

		ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage());
		return new ResponseEntity<>(error, ex.getStatusCode());
	}
}

package com.currencyexchange.ExchangeRateApi.exceptions;

import org.springframework.http.HttpStatus;

import com.currencyexchange.ExchangeRateApi.exceptions.interfaces.ICustomStatusException;

public class UsernameAlreadyExistsException extends RuntimeException implements ICustomStatusException {

  public UsernameAlreadyExistsException(String username) {
    super(String.format("Username '" + username + "' is already taken"));
  }

  @Override
  public HttpStatus getStatusCode() {
    return HttpStatus.CONFLICT;
  }
}

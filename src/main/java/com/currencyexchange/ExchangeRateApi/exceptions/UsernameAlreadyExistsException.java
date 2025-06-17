package com.currencyexchange.ExchangeRateApi.exceptions;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends RuntimeException {

  public UsernameAlreadyExistsException(String username) {
    super(String.format("Username '" + username + "' is already taken"));
  }

  public HttpStatus getStatusCode() {
    return HttpStatus.CONFLICT;
  }
}

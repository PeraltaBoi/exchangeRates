package com.currencyexchange.ExchangeRateApi.exceptions.interfaces;

import org.springframework.http.HttpStatus;

public interface ICustomStatusException {
  HttpStatus getStatusCode();

  String getMessage();
}

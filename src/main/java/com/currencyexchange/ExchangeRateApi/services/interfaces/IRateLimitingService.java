package com.currencyexchange.ExchangeRateApi.services.interfaces;

public interface IRateLimitingService {
  /**
   * Checks if a request is allowed for a given key (e.g., API key).
   *
   * @param key The identifier for the client (API Key, User ID, IP Address).
   * @return true if the request is allowed, false otherwise.
   */
  public boolean isAllowed(String key);
}

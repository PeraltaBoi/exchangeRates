package com.currencyexchange.ExchangeRateApi.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.currencyexchange.ExchangeRateApi.domain.ApiKeyRevokeStatus;
import com.currencyexchange.ExchangeRateApi.exceptions.ApiKeyNotFoundException;
import com.currencyexchange.ExchangeRateApi.exceptions.UnauthorizedException;
import com.currencyexchange.ExchangeRateApi.infrastructure.persistence.entities.User;

/**
 * Interface for authentication services,
 * providing methods for user and API key management
 */
public interface IAuthenticationService {
  /**
   * Signs up a new user with the provided username and password.
   *
   * @param username the username of the new user
   * @param password the password for the new user
   */
  void signUp(String username, String password);

  /**
   * Checks if the provided username and password match an existing user.
   *
   * @param username the username of the user
   * @param password the password of the user
   * @throws UnauthorizedException if the username or password is incorrect
   */
  void checkSignIn(String username, String password);

  /**
   * Retrieves a list of API keys associated with the specified user.
   *
   * @param username the username of the user
   * @param password the password of the user
   * @return a list of UUIDs representing the user's API keys
   * @throws UnauthorizedException if the username or password is incorrect
   */
  List<UUID> getUserKeys(String username, String password);

  /**
   * Generates a new API key for the specified user.
   *
   * @param username the username of the user
   * @param password the password of the user
   * @return a UUID representing the newly generated API key
   * @throws UnauthorizedException if the username or password is incorrect
   */
  UUID generateApiKey(String username, String password) throws UnauthorizedException;

  /**
   * Revokes an existing API.
   *
   * @param username the username of the user
   * @param password the password of the user
   * @param apiKey   the UUID of the API key to be revoked
   * @return ApiKeyRevokeStatus with success or the cause of failure
   */
  ApiKeyRevokeStatus revokeApiKey(String username, String password, UUID apiKey);

  /**
   * Checks if the provided API key is valid.
   *
   * @param apiKey the UUID of the API key to check
   * @return true if the API key is valid, false otherwise
   */
  boolean checkApiKey(UUID apiKey);

  /**
   * Retrieves the associated user for a given API key.
   *
   * @param apiKey the UUID of the API key to check
   * @return The associated
   *         {@link User}
   *         if the API key is valid
   * @throws ApiKeyNotFoundException if the API key is invalid
   */
  User getUserFromApiKey(UUID apiKey) throws ApiKeyNotFoundException;
}

package com.currencyexchange.ExchangeRateApi.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.currencyexchange.ExchangeRateApi.exceptions.UnauthorizedException;

/**
 * Interface for authentication services,
 * providing methods for user and API key management
 */
public interface IAuthenticationService {
  /**
   * Signs up a new user with the provided username and password.
   *
   * @param userName the username of the new user
   * @param password the password for the new user
   */
  void signUp(String userName, String password);

  /**
   * Checks if the provided username and password match an existing user.
   *
   * @param userName the username of the user
   * @param password the password of the user
   * @throws UnauthorizedException if the username or password is incorrect
   */
  void checkSignIn(String userName, String password);

  /**
   * Retrieves a list of API keys associated with the specified user.
   *
   * @param userName the username of the user
   * @param password the password of the user
   * @return a list of UUIDs representing the user's API keys
   * @throws UnauthorizedException if the username or password is incorrect
   */
  List<UUID> getUserKeys(String userName, String password);

  /**
   * Generates a new API key for the specified user.
   *
   * @param userName the username of the user
   * @param password the password of the user
   * @return a UUID representing the newly generated API key
   * @throws UnauthorizedException if the username or password is incorrect
   */
  UUID generateApiKey(String userName, String password);

  /**
   * Revokes an existing API.
   *
   * @param userName the username of the user
   * @param password the password of the user
   * @param apiKey   the UUID of the API key to be revoked
   * @return true if the API key was successfully revoked, false otherwise
   * @throws UnauthorizedException if the username, password, or API key is
   *                               incorrect
   */
  boolean revokeApiKey(String userName, String password, UUID apiKey);

  /**
   * Checks if the provided API key is valid.
   *
   * @param apiKey the UUID of the API key to check
   * @return true if the API key is valid, false otherwise
   */
  boolean checkApiKey(UUID apiKey);
}

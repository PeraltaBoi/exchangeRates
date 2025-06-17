package com.currencyexchange.ExchangeRateApi.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.currencyexchange.ExchangeRateApi.domain.ApiKeyRevokeStatus;
import com.currencyexchange.ExchangeRateApi.exceptions.UnauthorizedException;
import com.currencyexchange.ExchangeRateApi.exceptions.UsernameAlreadyExistsException;
import com.currencyexchange.ExchangeRateApi.infrastructure.persistence.entities.ApiKey;
import com.currencyexchange.ExchangeRateApi.infrastructure.persistence.entities.User;
import com.currencyexchange.ExchangeRateApi.infrastructure.persistence.repositories.ApiKeyRepository;
import com.currencyexchange.ExchangeRateApi.infrastructure.persistence.repositories.UserRepository;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IAuthenticationService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationService implements IAuthenticationService {

  private final UserRepository userRepository;
  private final ApiKeyRepository apiKeyRepository;
  private final PasswordEncoder passwordEncoder;

  public void signUp(String username, String password) {
    if (userRepository.existsByUsername(username)) {
      throw new UsernameAlreadyExistsException(username);
    }
    User user = User.builder()
        .username(username)
        .password(passwordEncoder.encode(password))
        .build();
    userRepository.save(user);
  }

  public void checkSignIn(String username, String password) {
    // if this throws an exception that means the login data is wrong
    getAuthenticatedUser(username, password);
  }

  public List<UUID> getUserKeys(String username, String password) {
    getAuthenticatedUser(username, password);

    return apiKeyRepository.findActiveKeysByUsername(username);
  }

  public UUID generateApiKey(String username, String password) {
    User user = getAuthenticatedUser(username, password);
    UUID newKey = UUID.randomUUID();
    ApiKey apiKey = ApiKey.builder()
        .key(newKey)
        .user(user)
        .revoked(false)
        .build();

    apiKeyRepository.save(apiKey);

    return newKey;
  }

  public ApiKeyRevokeStatus revokeApiKey(String username, String password, UUID apiKey) {
    User user;
    try {
      user = getAuthenticatedUser(username, password);
    } catch (UnauthorizedException e) {
      return ApiKeyRevokeStatus.INVALID_CREDENTIALS;
    }

    Optional<ApiKey> optionalKey = apiKeyRepository.findByKeyAndUser(apiKey, user);

    if (optionalKey.isEmpty()) {
      return ApiKeyRevokeStatus.KEY_NOT_FOUND;
    }

    ApiKey keyEntity = optionalKey.get();

    if (keyEntity.isRevoked()) {
      return ApiKeyRevokeStatus.KEY_ALREADY_REVOKED;
    }

    keyEntity.setRevoked(true);
    apiKeyRepository.save(keyEntity);

    return ApiKeyRevokeStatus.SUCCESS;
  }

  public boolean checkApiKey(UUID apiKey) {
        return apiKeyRepository.existsByKeyAndRevokedFalse(apiKey);
  }

  private User getAuthenticatedUser(String username, String password) {
    Optional<User> optionalUser = userRepository.findByUsername(username);

    if (optionalUser.isEmpty()) {
      throw new UnauthorizedException();
    }

    User user = optionalUser.get();

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new UnauthorizedException();
    }

    return user;
  }
}

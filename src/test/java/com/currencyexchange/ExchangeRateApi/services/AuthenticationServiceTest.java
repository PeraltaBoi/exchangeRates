package com.currencyexchange.ExchangeRateApi.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.currencyexchange.ExchangeRateApi.domain.ApiKeyRevokeStatus;
import com.currencyexchange.ExchangeRateApi.exceptions.ApiKeyNotFoundException;
import com.currencyexchange.ExchangeRateApi.exceptions.UnauthorizedException;
import com.currencyexchange.ExchangeRateApi.exceptions.UsernameAlreadyExistsException;
import com.currencyexchange.ExchangeRateApi.infrastructure.persistence.entities.ApiKey;
import com.currencyexchange.ExchangeRateApi.infrastructure.persistence.entities.User;
import com.currencyexchange.ExchangeRateApi.infrastructure.persistence.repositories.ApiKeyRepository;
import com.currencyexchange.ExchangeRateApi.infrastructure.persistence.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ApiKeyRepository apiKeyRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AuthenticationService authenticationService;

  @Captor
  private ArgumentCaptor<User> userCaptor;

  @Captor
  private ArgumentCaptor<ApiKey> apiKeyCaptor;

  private User testUser;
  private final String username = "testuser";
  private final String password = "password123";
  private final String hashedPassword = "hashedPassword123";

  @BeforeEach
  void setUp() {
    testUser = User.builder().id(1L).username(username).password(hashedPassword).build();
  }

  @Nested
  @DisplayName("signUp method tests")
  class SignUpTests {

    @Test
    void signUp_whenUsernameIsNew_shouldSaveNewUser() {
      // Arrange
      when(userRepository.existsByUsername(username)).thenReturn(false);
      when(passwordEncoder.encode(password)).thenReturn(hashedPassword);

      // Act
      authenticationService.signUp(username, password);

      // Assert
      verify(userRepository).save(userCaptor.capture());
      User savedUser = userCaptor.getValue();

      assertThat(savedUser.getUsername()).isEqualTo(username);
      assertThat(savedUser.getPassword()).isEqualTo(hashedPassword);
    }

    @Test
    void signUp_whenUsernameExists_shouldThrowException() {
      // Arrange
      when(userRepository.existsByUsername(username)).thenReturn(true);

      // Act & Assert
      assertThatThrownBy(() -> authenticationService.signUp(username, password))
          .isInstanceOf(UsernameAlreadyExistsException.class)
          .hasMessageContaining(username);

      verify(userRepository, never()).save(any(User.class));
    }
  }

  @Nested
  @DisplayName("generateApiKey method tests")
  class GenerateApiKeyTests {

    @Test
    void generateApiKey_withValidCredentials_shouldCreateAndReturnNewKey() {
      // Arrange
      when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
      when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);

      // Act
      UUID newKey = authenticationService.generateApiKey(username, password);

      // Assert
      assertThat(newKey).isNotNull();
      verify(apiKeyRepository).save(apiKeyCaptor.capture());
      ApiKey savedApiKey = apiKeyCaptor.getValue();

      assertThat(savedApiKey.getKey()).isEqualTo(newKey);
      assertThat(savedApiKey.getUser()).isEqualTo(testUser);
      assertThat(savedApiKey.isRevoked()).isFalse();
    }

    @Test
    void generateApiKey_withInvalidCredentials_shouldThrowException() {
      // Arrange
      when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(() -> authenticationService.generateApiKey(username, password))
          .isInstanceOf(UnauthorizedException.class);

      verify(apiKeyRepository, never()).save(any(ApiKey.class));
    }
  }

  @Nested
  @DisplayName("revokeApiKey method tests")
  class RevokeApiKeyTests {

    private final UUID keyToRevoke = UUID.randomUUID();

    @Test
    void revokeApiKey_withValidCredentialsAndKey_shouldSucceed() {
      // Arrange
      ApiKey activeKey = ApiKey.builder().key(keyToRevoke).user(testUser).revoked(false).build();
      when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
      when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
      when(apiKeyRepository.findByKeyAndUser(keyToRevoke, testUser)).thenReturn(Optional.of(activeKey));

      // Act
      ApiKeyRevokeStatus status = authenticationService.revokeApiKey(username, password, keyToRevoke);

      // Assert
      assertThat(status).isEqualTo(ApiKeyRevokeStatus.SUCCESS);
      verify(apiKeyRepository).save(apiKeyCaptor.capture());
      assertThat(apiKeyCaptor.getValue().isRevoked()).isTrue();
    }

    @Test
    void revokeApiKey_withInvalidCredentials_shouldReturnInvalidCredentialsStatus() {
      // Arrange
      when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
      when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false); // Wrong password

      // Act
      ApiKeyRevokeStatus status = authenticationService.revokeApiKey(username, password, keyToRevoke);

      // Assert
      assertThat(status).isEqualTo(ApiKeyRevokeStatus.INVALID_CREDENTIALS);
      verify(apiKeyRepository, never()).save(any(ApiKey.class));
    }

    @Test
    void revokeApiKey_whenKeyNotFound_shouldReturnKeyNotFoundStatus() {
      // Arrange
      when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
      when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
      when(apiKeyRepository.findByKeyAndUser(keyToRevoke, testUser)).thenReturn(Optional.empty());

      // Act
      ApiKeyRevokeStatus status = authenticationService.revokeApiKey(username, password, keyToRevoke);

      // Assert
      assertThat(status).isEqualTo(ApiKeyRevokeStatus.KEY_NOT_FOUND);
      verify(apiKeyRepository, never()).save(any(ApiKey.class));
    }

    @Test
    void revokeApiKey_whenKeyAlreadyRevoked_shouldReturnAlreadyRevokedStatus() {
      // Arrange
      ApiKey revokedKey = ApiKey.builder().key(keyToRevoke).user(testUser).revoked(true).build();
      when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
      when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
      when(apiKeyRepository.findByKeyAndUser(keyToRevoke, testUser)).thenReturn(Optional.of(revokedKey));

      // Act
      ApiKeyRevokeStatus status = authenticationService.revokeApiKey(username, password, keyToRevoke);

      // Assert
      assertThat(status).isEqualTo(ApiKeyRevokeStatus.KEY_ALREADY_REVOKED);
      verify(apiKeyRepository, never()).save(any(ApiKey.class));
    }
  }

  @Nested
  @DisplayName("checkApiKey method tests")
  class CheckApiKeyTests {

    @Test
    void checkApiKey_whenKeyIsValidAndActive_shouldReturnTrue() {
      // Arrange
      UUID validKey = UUID.randomUUID();
      when(apiKeyRepository.existsByKeyAndRevokedFalse(validKey)).thenReturn(true);

      // Act
      boolean result = authenticationService.checkApiKey(validKey);

      // Assert
      assertThat(result).isTrue();
    }

    @Test
    void checkApiKey_whenKeyIsInvalidOrRevoked_shouldReturnFalse() {
      // Arrange
      UUID invalidKey = UUID.randomUUID();
      when(apiKeyRepository.existsByKeyAndRevokedFalse(invalidKey)).thenReturn(false);

      // Act
      boolean result = authenticationService.checkApiKey(invalidKey);

      // Assert
      assertThat(result).isFalse();
    }
  }

  @Nested
  @DisplayName("getUserFromApiKey method tests")
  class GetUserFromApiKeyTests {

    @Test
    void getUserFromApiKey_whenKeyExists_shouldReturnUser() {
      // Arrange
      UUID validKey = UUID.randomUUID();
      ApiKey apiKey = ApiKey.builder().key(validKey).user(testUser).build();
      when(apiKeyRepository.findByKey(validKey)).thenReturn(Optional.of(apiKey));

      // Act
      User foundUser = authenticationService.getUserFromApiKey(validKey);

      // Assert
      assertThat(foundUser).isEqualTo(testUser);
    }

    @Test
    void getUserFromApiKey_whenKeyDoesNotExist_shouldThrowException() {
      // Arrange
      UUID invalidKey = UUID.randomUUID();
      when(apiKeyRepository.findByKey(invalidKey)).thenReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(() -> authenticationService.getUserFromApiKey(invalidKey))
          .isInstanceOf(ApiKeyNotFoundException.class);
    }
  }
}

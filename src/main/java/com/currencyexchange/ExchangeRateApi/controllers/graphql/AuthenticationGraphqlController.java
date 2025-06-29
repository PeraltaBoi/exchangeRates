package com.currencyexchange.ExchangeRateApi.controllers.graphql;

import java.util.List;
import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.currencyexchange.ExchangeRateApi.contracts.internal.rest.requests.UserAuthRequestDTO;
import com.currencyexchange.ExchangeRateApi.domain.ApiKeyRevokeStatus;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IAuthenticationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthenticationGraphqlController {

  private final IAuthenticationService authenticationService;

  // === Mutations ===

  @MutationMapping
  public Boolean signUp(@Argument("credentials") UserAuthRequestDTO authDTO) {
    authenticationService.signUp(authDTO.getUsername(), authDTO.getPassword());
    return true; // Success if no exception is thrown
  }

  @MutationMapping
  public Boolean checkSignIn(
      @Argument("credentials") UserAuthRequestDTO authDTO) {
    authenticationService.checkSignIn(
        authDTO.getUsername(),
        authDTO.getPassword());
    return true; // Success if no exception is thrown
  }

  @MutationMapping
  public UUID generateApiKey(
      @Argument("credentials") UserAuthRequestDTO authDTO) {
    return authenticationService.generateApiKey(
        authDTO.getUsername(),
        authDTO.getPassword());
  }

  @MutationMapping
  public Boolean revokeApiKey(
      @Argument UUID key,
      @Argument("credentials") UserAuthRequestDTO authDTO) {
    ApiKeyRevokeStatus status = authenticationService.revokeApiKey(
        authDTO.getUsername(),
        authDTO.getPassword(),
        key);
    // For GraphQL, it's often best to let exceptions handle detailed errors
    // and return a simple boolean for the happy path.
    return status == ApiKeyRevokeStatus.SUCCESS;
  }

  // === Queries ===

  @QueryMapping
  public List<UUID> userKeys(
      @Argument("credentials") UserAuthRequestDTO authDTO) {
    return authenticationService.getUserKeys(
        authDTO.getUsername(),
        authDTO.getPassword());
  }

  @QueryMapping
  public Boolean checkApiKey(@Argument UUID key) {
    return authenticationService.checkApiKey(key);
  }
}

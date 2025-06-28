package com.currencyexchange.ExchangeRateApi.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.currencyexchange.ExchangeRateApi.contracts.internal.rest.requests.UserAuthRequestDTO;
import com.currencyexchange.ExchangeRateApi.domain.ApiKeyRevokeStatus;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IAuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  // a few methods from this service only return void
  // if they return successfully then everything went ok
  // otherwise, they throw exceptions that get handled
  // by the global exception handler
  private final IAuthenticationService authenticationService;

  @PostMapping("/signUp")
  public ResponseEntity<Boolean> signUp(
      @Valid @RequestBody UserAuthRequestDTO authDTO) {
    authenticationService.signUp(authDTO.getUsername(), authDTO.getPassword());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/checkSignIn")
  public ResponseEntity<Void> checkSignIn(
      @RequestBody UserAuthRequestDTO authDTO) {
    authenticationService.checkSignIn(authDTO.getUsername(), authDTO.getPassword());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/keys")
  public ResponseEntity<List<UUID>> getUserKeys(
      // this should be a GET request
      // but GET requests shouldn't have bodies,
      // and i don't want to include the password in the url
      @RequestBody UserAuthRequestDTO authDTO) {
    return ResponseEntity.ok(authenticationService.getUserKeys(authDTO.getUsername(), authDTO.getPassword()));
  }

  @PostMapping("/key/generate")
  public ResponseEntity<UUID> generateApiKey(
      @RequestBody UserAuthRequestDTO authDTO) {
    return ResponseEntity.ok(authenticationService.generateApiKey(authDTO.getUsername(), authDTO.getPassword()));
  }

  @DeleteMapping("/key/revoke/{key}")
  public ResponseEntity<Void> revokeApiKey(
      @PathVariable("key") UUID apiKey,
      @RequestBody UserAuthRequestDTO authDTO) {
    ApiKeyRevokeStatus status = authenticationService.revokeApiKey(
        authDTO.getUsername(),
        authDTO.getPassword(),
        apiKey);

    return switch (status) {
      case SUCCESS -> ResponseEntity.ok().build();
      case INVALID_CREDENTIALS -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      case KEY_NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      case KEY_ALREADY_REVOKED -> ResponseEntity.status(HttpStatus.CONFLICT).build();
    };
  }

  @GetMapping("/key/{key}/validate")
  public ResponseEntity<Boolean> checkApiKey(
      @PathVariable("key") UUID apiKey) {
    return ResponseEntity.ok(authenticationService.checkApiKey(apiKey));
  }
}

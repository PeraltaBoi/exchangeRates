package com.currencyexchange.ExchangeRateApi.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.currencyexchange.ExchangeRateApi.contracts.internal.rest.requests.UserAuthRequestDTO;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IAuthenticationService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  // a few methods from this service only return void
  // if they return successfuly then everything went ok
  // otherwise, they throw exceptions that get handled
  // by the global exception handler
  private final IAuthenticationService authenticationService;

  @PostMapping("/signUp")
  public ResponseEntity<Boolean> signUp(
      @RequestBody UserAuthRequestDTO authDTO) {
    authenticationService.signUp(authDTO.getUsername(), authDTO.getPassword());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/checkSignIn")
  public ResponseEntity<Void> checkSignIn(
      @RequestBody UserAuthRequestDTO authDTO) {
    authenticationService.checkSignIn(authDTO.getUsername(), authDTO.getPassword());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/keys")
  public ResponseEntity<List<UUID>> getUserKeys(
      // GET requests shouldn't have bodies,
      // but i don't want to include the password in the url
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
    authenticationService.revokeApiKey(authDTO.getUsername(), authDTO.getPassword(), apiKey);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/key/{key}/validate")
  public ResponseEntity<Boolean> checkApiKey(
      @PathVariable("key") UUID apiKey) {
    return ResponseEntity.ok(authenticationService.checkApiKey(apiKey));
  }
}

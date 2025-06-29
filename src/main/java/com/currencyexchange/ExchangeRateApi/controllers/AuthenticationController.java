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
import com.currencyexchange.ExchangeRateApi.contracts.internal.rest.responses.ErrorResponseDTO;
import com.currencyexchange.ExchangeRateApi.domain.ApiKeyRevokeStatus;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IAuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
  @Operation(summary = "Register a new user", description = "Creates a new user account with the provided username and password. The service method throws exceptions for failures like a duplicate username, which are handled by a global exception handler.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., username too short, password doesn't meet criteria)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
      @ApiResponse(responseCode = "409", description = "Username already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
  })
  public ResponseEntity<Void> signUp(
      @Parameter(description = "User credentials for registration", required = true) @Valid @RequestBody UserAuthRequestDTO authDTO) {
    authenticationService.signUp(authDTO.getUsername(), authDTO.getPassword());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/checkSignIn")
  @Operation(summary = "Verify user credentials", description = "Checks if the provided username and password are valid for sign-in. Does not create a session or token. Throws an exception for invalid credentials.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Credentials are valid", content = @Content),
      @ApiResponse(responseCode = "401", description = "Invalid username or password", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
  })
  public ResponseEntity<Void> checkSignIn(
      @Parameter(description = "User credentials to verify", required = true) @RequestBody UserAuthRequestDTO authDTO) {
    authenticationService.checkSignIn(
        authDTO.getUsername(),
        authDTO.getPassword());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/keys")
  @Operation(summary = "Get all API keys for a user", description = "Retrieves a list of all active API key UUIDs for the specified user. Requires user credentials for authentication. Note: This uses a POST request to avoid sending credentials in the URL.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "API keys retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
      @ApiResponse(responseCode = "401", description = "Invalid username or password", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
  })
  public ResponseEntity<List<UUID>> getUserKeys(
      @Parameter(description = "User credentials to authenticate the request", required = true) @RequestBody UserAuthRequestDTO authDTO) {
    return ResponseEntity.ok(
        authenticationService.getUserKeys(
            authDTO.getUsername(),
            authDTO.getPassword()));
  }

  @PostMapping("/key/generate")
  @Operation(summary = "Generate a new API key", description = "Generates a new API key for the specified user. Requires user credentials for authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "New API key generated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UUID.class))),
      @ApiResponse(responseCode = "401", description = "Invalid username or password", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
  })
  public ResponseEntity<UUID> generateApiKey(
      @Parameter(description = "User credentials to authenticate the request", required = true) @RequestBody UserAuthRequestDTO authDTO) {
    return ResponseEntity.ok(
        authenticationService.generateApiKey(
            authDTO.getUsername(),
            authDTO.getPassword()));
  }

  @DeleteMapping("/key/revoke/{key}")
  @Operation(summary = "Revoke an API key", description = "Revokes an existing API key, making it invalid for future use. This action is irreversible. Requires user credentials for authentication.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "API key revoked successfully", content = @Content),
      @ApiResponse(responseCode = "401", description = "Invalid user credentials provided", content = @Content),
      @ApiResponse(responseCode = "404", description = "The specified API key was not found for this user", content = @Content),
      @ApiResponse(responseCode = "409", description = "The specified API key has already been revoked", content = @Content),
  })
  public ResponseEntity<Void> revokeApiKey(
      @Parameter(description = "The UUID of the API key to revoke", required = true) @PathVariable("key") UUID apiKey,
      @Parameter(description = "User credentials to authenticate the request", required = true) @RequestBody UserAuthRequestDTO authDTO) {
    ApiKeyRevokeStatus status = authenticationService.revokeApiKey(
        authDTO.getUsername(),
        authDTO.getPassword(),
        apiKey);

    return switch (status) {
      case SUCCESS -> ResponseEntity.ok().build();
      case INVALID_CREDENTIALS -> ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .build();
      case KEY_NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      case KEY_ALREADY_REVOKED -> ResponseEntity
          .status(HttpStatus.CONFLICT)
          .build();
    };
  }

  @GetMapping("/key/{key}/validate")
  @Operation(summary = "Validate an API key", description = "Checks if a given API key is valid and active. This endpoint is typically used by services to authorize requests.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "API key validity check complete. Returns true if the key is valid, false otherwise.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
  })
  public ResponseEntity<Boolean> checkApiKey(
      @Parameter(description = "The UUID of the API key to validate", required = true) @PathVariable("key") UUID apiKey) {
    return ResponseEntity.ok(authenticationService.checkApiKey(apiKey));
  }
}

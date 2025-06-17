package com.currencyexchange.ExchangeRateApi.contracts.internal.rest.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request body for user authentication operations")
public class UserAuthRequestDTO {

  @NotBlank(message = "Username is required")
  @Schema(description = "Account's username", example = "john_doe")
  private String username;

  @NotBlank(message = "Password is required")
  @Schema(description = "Account' password", example = "SecurePass123!")
  private String password;
}

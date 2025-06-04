package com.currencyexchange.ExchangeRateApi.contracts.internal.rest.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response")
public record ErrorResponseDTO(
		@Schema(description = "Error message", example = "No exchange rates found for USD") String message) {
}

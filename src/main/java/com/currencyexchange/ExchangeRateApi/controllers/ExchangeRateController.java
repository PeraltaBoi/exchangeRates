package com.currencyexchange.ExchangeRateApi.controllers;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.currencyexchange.ExchangeRateApi.contracts.internal.responses.ErrorResponse;
import com.currencyexchange.ExchangeRateApi.contracts.internal.responses.ExchangeRateResponseDTO;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IRateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/exchange")
@RequiredArgsConstructor
public class ExchangeRateController {

	private final IRateService rateService;

	@GetMapping("/rates")
	@Operation(summary = "Get exchange rates", description = "Retrieve exchange rate(s) for a given base currency. Optionally specify a target currency to get a single conversion rate.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Exchange rate(s) retrieved successfully", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExchangeRateResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "No rates found for requested currency/currencies", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<ExchangeRateResponseDTO> getAllRates(
			@Parameter(description = "Base currency code (e.g., USD)", required = true) @RequestParam String from,
			@Parameter(description = "Target currency code (e.g., EUR). If not provided, all exchange rates will be returned.") @RequestParam Optional<String> to) {
		String fromCurrency = from.toUpperCase();
		if (to.isPresent()) {
			String toCurrency = to.get().toUpperCase();
			BigDecimal rate = rateService.getExchangeRate(fromCurrency, toCurrency);
			return ResponseEntity.ok(
					new ExchangeRateResponseDTO(fromCurrency, Map.of(toCurrency, rate)));
		} else {
			var rates = rateService.getAllExchangeRates(fromCurrency);
			return ResponseEntity.ok(new ExchangeRateResponseDTO(fromCurrency, rates));
		}
	}
}

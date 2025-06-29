package com.currencyexchange.ExchangeRateApi.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.currencyexchange.ExchangeRateApi.contracts.internal.rest.responses.ConvertedAmountsResponseDTO;
import com.currencyexchange.ExchangeRateApi.contracts.internal.rest.responses.ErrorResponseDTO;
import com.currencyexchange.ExchangeRateApi.contracts.internal.rest.responses.ExchangeRatesResponseDTO;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IRateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/exchange")
@RequiredArgsConstructor
public class ExchangeRateController {

	private final IRateService rateService;

	@GetMapping("/rates")
	@Operation(summary = "Get exchange rates", description = "Retrieve exchange rate(s) for a given base currency. Optionally specify a target currency to get a single conversion rate.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Exchange rate(s) retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExchangeRatesResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "No rates found for requested currency/currencies", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
	})
	public ResponseEntity<ExchangeRatesResponseDTO> getRates(
			@Parameter(description = "Base currency code (e.g., USD)", required = true) @RequestParam String from,
			@Parameter(description = "Target currency code (e.g., EUR). If not provided, all exchange rates will be returned.") @RequestParam Optional<String> to) {
		String fromCurrency = from.toUpperCase();
		if (to.isPresent()) {
			String toCurrency = to.get().toUpperCase();
			BigDecimal rate = rateService.getExchangeRate(fromCurrency, toCurrency);
			return ResponseEntity.ok(
					new ExchangeRatesResponseDTO(fromCurrency, Map.of(toCurrency, rate)));
		} else {
			var rates = rateService.getAllExchangeRates(fromCurrency);
			return ResponseEntity.ok(new ExchangeRatesResponseDTO(fromCurrency, rates));
		}
	}

	@GetMapping("/convert")
	@Operation(summary = "Convert some amount between currencies")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Value converted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConvertedAmountsResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "No rates found for requested currency/currencies", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
	})
	public ResponseEntity<ConvertedAmountsResponseDTO> ConvertAmount(
			@Parameter(description = "Base currency code (e.g., USD)", required = true) @RequestParam String from,
			@Parameter(description = "Target currency code (e.g., EUR)", required = true) @RequestParam List<String> to,
			@Parameter(description = "Amount to be converted", required = true) @RequestParam BigDecimal amount) {
		String fromCurrency = from.toUpperCase();
		List<String> toCurrencies = to.stream()
				.map(String::toUpperCase)
				.collect(Collectors.toList());
		var amounts = rateService.convertAmount(amount, fromCurrency, toCurrencies);
		return ResponseEntity.ok(new ConvertedAmountsResponseDTO(fromCurrency, amounts));
	}
}

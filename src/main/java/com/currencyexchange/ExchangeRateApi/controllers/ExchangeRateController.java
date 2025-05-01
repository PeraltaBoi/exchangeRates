package com.currencyexchange.ExchangeRateApi.controllers;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.currencyexchange.ExchangeRateApi.contracts.internal.responses.ExchangeRateResponse;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IRateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/exchange")
@RequiredArgsConstructor
public class ExchangeRateController {

	private final IRateService rateService;

	@GetMapping("/rates")
	public ResponseEntity<ExchangeRateResponse> getAllRates(@RequestParam String from) {
		return rateService.getAllExchangeRates(from)
				.map(rates -> ResponseEntity.ok(ExchangeRateResponse.builder()
						.from(from)
						.rates(rates.getQuotes().entrySet().stream()
								.collect(Collectors.toMap(
										entry -> entry.getKey().getTo(),
										Map.Entry::getValue)))
						.build()))
				.orElse(ResponseEntity.notFound().build());
	}
}

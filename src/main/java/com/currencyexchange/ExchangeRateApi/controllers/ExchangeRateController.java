package com.currencyexchange.ExchangeRateApi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.currencyexchange.ExchangeRateApi.domain.ExchangeRates;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IRateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/exchange")
@RequiredArgsConstructor
public class ExchangeRateController {

	private final IRateService rateService;

	@GetMapping("/rates")
	public ResponseEntity<ExchangeRates> getAllRates(@RequestParam String from) {
		return rateService.getAllExchangeRates(from)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}

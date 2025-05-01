package com.currencyexchange.ExchangeRateApi.infrastructure.exchanges.frankfurter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.currencyexchange.ExchangeRateApi.contracts.external.responses.FrankfurterResponseDTO;
import com.currencyexchange.ExchangeRateApi.domain.CurrencyPair;
import com.currencyexchange.ExchangeRateApi.domain.ExchangeRatesFromBase;
import com.currencyexchange.ExchangeRateApi.infrastructure.exchanges.IExchangeRateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Primary
@Qualifier("frankfurter")
public class FrankfurterClient implements IExchangeRateProvider {

	private final RestTemplate restTemplate;

	@Value("${frankfurter.base-url:https://api.frankfurter.dev/v1}")
	private String baseUrl;

	@Override
	public Optional<ExchangeRatesFromBase> getAllRates() {
		try {
			String url = UriComponentsBuilder.fromUriString(baseUrl)
					.path("/latest")
					.build()
					.toUriString();

			FrankfurterResponseDTO response = restTemplate.getForObject(url, FrankfurterResponseDTO.class);

			if (response == null) {
				log.error("Failed to get exchange rates from Frankfurter");
				return Optional.empty();
			}

			Map<CurrencyPair, BigDecimal> transformedRates = response.getRates().entrySet().stream()
					.collect(Collectors.toMap(
							entry -> new CurrencyPair(response.getBase(), entry.getKey()),
							Map.Entry::getValue));

			return Optional.of(new ExchangeRatesFromBase(
					response.getBase(),
					transformedRates));

		} catch (Exception e) {
			log.error("Error fetching exchange rates from Frankfurter", e);
			return Optional.empty();
		}
	}

	@Override
	public String getProviderName() {
		return "Frankfurter";
	}
}

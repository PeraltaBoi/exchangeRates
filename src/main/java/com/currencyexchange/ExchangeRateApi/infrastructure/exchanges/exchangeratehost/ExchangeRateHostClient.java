package com.currencyexchange.ExchangeRateApi.infrastructure.exchanges.exchangeratehost;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.currencyexchange.ExchangeRateApi.contracts.external.responses.ExchangeRateHostResponse;
import com.currencyexchange.ExchangeRateApi.domain.ExchangeRatesFromBase;
import com.currencyexchange.ExchangeRateApi.infrastructure.exchanges.IExchangeRate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateHostClient implements IExchangeRate {

	private final RestTemplate restTemplate;

	@Value("${exchange-rate.host.base-url:https://api.exchangerate.host}")
	private String baseUrl;

	@Override
	public Optional<ExchangeRatesFromBase> getAllRates(String sourceCurrency) {
		try {
			String url = UriComponentsBuilder.fromUriString(baseUrl)
					.path("/live")
					.queryParam("source", sourceCurrency)
					.build()
					.toUriString();

			ExchangeRateHostResponse response = restTemplate.getForObject(url, ExchangeRateHostResponse.class);

			if (response == null || !response.isSuccess()) {
				log.error("Failed to get exchange rates from ExchangeRate.host for source currency: {}", sourceCurrency);
				return Optional.empty();
			}

			return Optional.of(new ExchangeRatesFromBase(
					response.getSource(),
					response.getQuotes()));

		} catch (Exception e) {
			log.error("Error fetching exchange rates from ExchangeRate.host", e);
			return Optional.empty();
		}
	}

	@Override
	public String getProviderName() {
		return "ExchangeRate.host";
	}
}

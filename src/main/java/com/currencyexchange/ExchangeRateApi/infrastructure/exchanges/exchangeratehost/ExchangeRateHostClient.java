package com.currencyexchange.ExchangeRateApi.infrastructure.exchanges.exchangeratehost;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.currencyexchange.ExchangeRateApi.contracts.external.responses.ExchangeRateHostResponseDTO;
import com.currencyexchange.ExchangeRateApi.domain.CurrencyPair;
import com.currencyexchange.ExchangeRateApi.domain.ExchangeRatesFromBase;
import com.currencyexchange.ExchangeRateApi.infrastructure.exchanges.IExchangeRateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("exchangeRateHost")
public class ExchangeRateHostClient implements IExchangeRateProvider {

	private final RestTemplate restTemplate;

	@Value("${exchange-rate.host.base-url:https://api.exchangerate.host}")
	private String baseUrl;

	@Override
	public Optional<ExchangeRatesFromBase> getAllRates() {
		try {
			String url = UriComponentsBuilder.fromUriString(baseUrl)
					.path("/live")
					.build()
					.toUriString();

			ExchangeRateHostResponseDTO response = restTemplate.getForObject(url, ExchangeRateHostResponseDTO.class);

			if (response == null || !response.isSuccess()) {
				log.error("Failed to get exchange rates from ExchangeRate.host");
				return Optional.empty();
			}

			// the collect bellow converts Strings of type "ABCXYZ"
			// into a CurrencyPair that has "ABC" as the From and "XYZ" as the To
			return Optional.of(new ExchangeRatesFromBase(
					response.getSource(),
					response.getQuotes().entrySet().stream()
							.collect(Collectors.toMap(
									entry -> new CurrencyPair(entry.getKey().substring(0,3), entry.getKey().substring(3)),
									Map.Entry::getValue))
			));

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

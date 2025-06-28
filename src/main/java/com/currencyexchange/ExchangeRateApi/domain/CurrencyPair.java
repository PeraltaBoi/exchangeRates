package com.currencyexchange.ExchangeRateApi.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class CurrencyPair {
	@NonNull
	private String from;
	@NonNull
	private String to;

	@JsonCreator
	public static CurrencyPair fromString(String pair) {
		if (pair == null || !pair.contains("-")) {
			throw new IllegalArgumentException(
					"CurrencyPair key must be in the format 'FROM-TO'");
		}
		String[] parts = pair.split("-");
		return new CurrencyPair(parts[0], parts[1]);
	}

	/**
	 * This method is used by Jackson during serialization to create the
	 * string key for the map. The @JsonValue annotation tells Jackson to use
	 * the return value of this method as the representation of the object.
	 *
	 * @return A clean string representation, e.g., "USD-EUR".
	 */
	@Override
	@JsonValue
	public String toString() {
		return from + "-" + to;
	}
}

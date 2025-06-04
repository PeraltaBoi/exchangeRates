package com.currencyexchange.ExchangeRateApi.contracts.internal.rest.responses;

import java.math.BigDecimal;
import java.util.Map;

public record ConvertedAmountsResponseDTO(
    String from,
    Map<String, BigDecimal> amounts) {
}

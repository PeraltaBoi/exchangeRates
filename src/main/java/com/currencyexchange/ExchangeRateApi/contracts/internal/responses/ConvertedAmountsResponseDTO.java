package com.currencyexchange.ExchangeRateApi.contracts.internal.responses;

import java.math.BigDecimal;
import java.util.Map;

public record ConvertedAmountsResponseDTO(
    String from,
    Map<String, BigDecimal> amounts) {
}

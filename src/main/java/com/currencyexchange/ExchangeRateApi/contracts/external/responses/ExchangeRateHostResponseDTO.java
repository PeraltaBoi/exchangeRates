package com.currencyexchange.ExchangeRateApi.contracts.external.responses;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class ExchangeRateHostResponseDTO {
    private boolean success;
    private String terms;
    private String privacy;
    private long timestamp;
    private String source;
    private Map<String, BigDecimal> quotes;
}

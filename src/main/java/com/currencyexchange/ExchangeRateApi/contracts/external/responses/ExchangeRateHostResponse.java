package com.currencyexchange.ExchangeRateApi.contracts.external.responses;

import java.util.Map;
import lombok.Data;

@Data
public class ExchangeRateHostResponse {
    private boolean success;
    private String terms;
    private String privacy;
    private long timestamp;
    private String source;
    private Map<String, Double> quotes;
}

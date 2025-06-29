package com.currencyexchange.ExchangeRateApi.controllers.graphql;

import com.currencyexchange.ExchangeRateApi.domain.ExchangeRates;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IRateService;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ExchangeRateGraphqlController {

  private final IRateService rateService;

  public record CurrencyRate(String currency, BigDecimal rate) {
  }

  public record ConvertedAmount(String currency, BigDecimal amount) {
  }

  @QueryMapping
  public List<CurrencyRate> rates(
      @Argument String from,
      @Argument String to) {
    String fromCurrency = from.toUpperCase();

    if (to != null) {
      String toCurrency = to.toUpperCase();
      BigDecimal rate = rateService.getExchangeRate(fromCurrency, toCurrency);
      return List.of(new CurrencyRate(toCurrency, rate));
    } else {
      ExchangeRates exchangeRates = rateService.getAllExchangeRates(
          fromCurrency);

      return exchangeRates
          .getQuotes()
          .entrySet()
          .stream()
          .map(entry -> new CurrencyRate(entry.getKey().getTo(), entry.getValue()))
          .collect(Collectors.toList());
    }
  }

  @QueryMapping
  public List<ConvertedAmount> convert(
      @Argument String from,
      @Argument List<String> to,
      @Argument BigDecimal amount) {
    String fromCurrency = from.toUpperCase();
    List<String> toCurrencies = to
        .stream()
        .map(String::toUpperCase)
        .collect(Collectors.toList());

    var amountsMap = rateService.convertAmount(
        amount,
        fromCurrency,
        toCurrencies);

    return amountsMap
        .entrySet()
        .stream()
        .map(entry -> new ConvertedAmount(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
  }
}

package com.currencyexchange.ExchangeRateApi.filters;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.web.filter.OncePerRequestFilter;

import com.currencyexchange.ExchangeRateApi.exceptions.ApiKeyNotFoundException;
import com.currencyexchange.ExchangeRateApi.infrastructure.persistence.entities.User;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IAuthenticationService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

  private final IAuthenticationService authenticationService;
  private final String headerName;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    UUID apiKeyValue = Optional.ofNullable(request.getHeader(headerName))
        .map(UUID::fromString)
        .orElseThrow(() -> new ApiKeyNotFoundException("Invalid API key format"));

    User user = authenticationService.getUserFromApiKey(apiKeyValue);

    request.setAttribute("authenticatedUser", user);

    filterChain.doFilter(request, response);
  }
}

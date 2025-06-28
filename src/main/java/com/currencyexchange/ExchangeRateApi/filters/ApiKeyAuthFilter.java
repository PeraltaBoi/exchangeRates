package com.currencyexchange.ExchangeRateApi.filters;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

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
        .map(UUID::fromString).orElse(null);

    if (apiKeyValue == null) {
      filterChain.doFilter(request, response);
      return;
    }

    User user = authenticationService.getUserFromApiKey(apiKeyValue);

    if (user != null) {
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
          Collections.emptyList());
      SecurityContextHolder.getContext().setAuthentication(authentication);

      request.setAttribute("authenticatedUser", user);
    } else {
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }
}

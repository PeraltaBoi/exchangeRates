package com.currencyexchange.ExchangeRateApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.currencyexchange.ExchangeRateApi.filters.ApiKeyAuthFilter;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IAuthenticationService;
import com.currencyexchange.ExchangeRateApi.services.interfaces.IRateLimitingService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final IAuthenticationService authenticationService;
  private final IRateLimitingService rateLimitingService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // this is broken for some reason
            .requestMatchers("/api/v1/auth/**", "/swagger-ui/**").permitAll()
            .requestMatchers("/api/**").authenticated()
            .anyRequest().permitAll())
        .addFilterBefore(new ApiKeyAuthFilter(authenticationService, rateLimitingService, "X-API-KEY"),
            UsernamePasswordAuthenticationFilter.class)
        .formLogin(form -> form.disable());

    return http.build();
  }
}

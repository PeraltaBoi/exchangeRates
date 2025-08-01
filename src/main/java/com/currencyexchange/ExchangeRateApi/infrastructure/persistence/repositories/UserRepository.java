package com.currencyexchange.ExchangeRateApi.infrastructure.persistence.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.currencyexchange.ExchangeRateApi.infrastructure.persistence.entities.User;

public interface UserRepository extends JpaRepository<User, String> {
  boolean existsByUsername(String username);
  Optional<User> findByUsername(String username);
  // Optional<User> findByApiKey(UUID apiKey);
}

package com.currencyexchange.ExchangeRateApi.infrastructure.persistence.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.currencyexchange.ExchangeRateApi.infrastructure.persistence.entities.ApiKey;
import com.currencyexchange.ExchangeRateApi.infrastructure.persistence.entities.User;

public interface ApiKeyRepository extends JpaRepository<ApiKey, String> {
  Optional<ApiKey> findByKeyAndRevokedFalse(UUID key);
  @Query("SELECT a.key FROM ApiKey a WHERE a.user.username = :username AND a.revoked = false")
  List<UUID> findActiveKeysByUsername(@Param("username") String username);
  Optional<ApiKey> findByKeyAndUser(UUID key, User user);
  boolean existsByKeyAndRevokedFalse(UUID key);
}

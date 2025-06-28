package com.currencyexchange.ExchangeRateApi.infrastructure.persistence.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_keys")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {
  @Id
  @Column(name = "api_key", updatable = false, nullable = false)
  private UUID key;
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  private boolean revoked;
}

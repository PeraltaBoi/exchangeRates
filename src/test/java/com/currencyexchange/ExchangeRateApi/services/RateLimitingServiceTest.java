package com.currencyexchange.ExchangeRateApi.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;

@ExtendWith(MockitoExtension.class)
class RateLimitingServiceTest {

  private static final String TEST_KEY = "test-api-key";
  private static final int LIMIT = 3;
  private static final int WINDOW_IN_SEC = 60;

  @Mock
  private CacheManager cacheManager;

  @Mock
  private Cache cache;

  @Captor
  private ArgumentCaptor<List<Long>> timestampsCaptor;

  private RateLimitingService rateLimitingService;

  // To control the time, we mock the static Instant.now() method
  private MockedStatic<Instant> mockedInstant;

  @BeforeEach
  void setUp() {
    // Arrange: Ensure the cacheManager returns our mocked cache
    when(cacheManager.getCache(RateLimitingService.CACHE_NAME)).thenReturn(cache);

    // Instantiate the service with our mocks and test values
    rateLimitingService = new RateLimitingService(
      cacheManager,
      LIMIT,
      WINDOW_IN_SEC
    );
  }

  @AfterEach
  void tearDown() {
    // Clean up the static mock after each test to avoid side-effects
    if (mockedInstant != null) {
      mockedInstant.close();
    }
  }

  private void setupMockedTime(long epochMilli) {
    // This helper method sets up a fixed point in time
    Instant fixedInstant = Instant.ofEpochMilli(epochMilli);
    mockedInstant = Mockito.mockStatic(Instant.class);
    mockedInstant.when(Instant::now).thenReturn(fixedInstant);
  }

  @Test
  void isAllowed_whenFirstRequest_shouldReturnTrueAndCacheTimestamp() {
    // Arrange
    long now = System.currentTimeMillis();
    setupMockedTime(now);
    when(cache.get(TEST_KEY)).thenReturn(null); // No previous requests

    // Act
    boolean isAllowed = rateLimitingService.isAllowed(TEST_KEY);

    // Assert
    assertThat(isAllowed).isTrue();

    // Verify that we put a new list with one timestamp into the cache
    verify(cache).put(eq(TEST_KEY), timestampsCaptor.capture());
    List<Long> capturedTimestamps = timestampsCaptor.getValue();
    assertThat(capturedTimestamps).hasSize(1).containsExactly(now);
  }

  @Test
  void isAllowed_whenUnderLimit_shouldReturnTrue() {
    // Arrange
    long now = System.currentTimeMillis();
    setupMockedTime(now);

    // Simulate one previous request made 10 seconds ago
    List<Long> existingTimestamps = new LinkedList<>();
    existingTimestamps.add(now - 10_000L);
    when(cache.get(TEST_KEY)).thenReturn(new SimpleValueWrapper(existingTimestamps));

    // Act
    boolean isAllowed = rateLimitingService.isAllowed(TEST_KEY);

    // Assert
    assertThat(isAllowed).isTrue();

    // Verify the cache was updated with the new timestamp added
    verify(cache).put(eq(TEST_KEY), timestampsCaptor.capture());
    List<Long> capturedTimestamps = timestampsCaptor.getValue();
    assertThat(capturedTimestamps).hasSize(2).contains(now - 10_000L, now);
  }

  @Test
  void isAllowed_whenAtLimit_shouldReturnFalse() {
    // Arrange
    long now = System.currentTimeMillis();
    setupMockedTime(now);

    // Simulate a full cache with 3 recent requests
    List<Long> existingTimestamps = new LinkedList<>();
    existingTimestamps.add(now - 30_000L);
    existingTimestamps.add(now - 20_000L);
    existingTimestamps.add(now - 10_000L);
    when(cache.get(TEST_KEY)).thenReturn(new SimpleValueWrapper(existingTimestamps));

    // Act
    boolean isAllowed = rateLimitingService.isAllowed(TEST_KEY);

    // Assert
    assertThat(isAllowed).isFalse();

    // IMPORTANT: Verify that the cache was NOT updated when the request is denied
    verify(cache, never()).put(any(), any());
  }

  @Test
  void isAllowed_whenWindowExpires_shouldPruneOldTimestampsAndAllowRequest() {
    // Arrange
    long now = System.currentTimeMillis();
    setupMockedTime(now); // The current request happens at 'now'

    // Simulate a full cache, but two timestamps are outside the 60-second window
    List<Long> existingTimestamps = new LinkedList<>();
    existingTimestamps.add(now - 70_000L); // Expired
    existingTimestamps.add(now - 65_000L); // Expired
    existingTimestamps.add(now - 10_000L); // Still valid
    when(cache.get(TEST_KEY)).thenReturn(new SimpleValueWrapper(existingTimestamps));

    // Act
    boolean isAllowed = rateLimitingService.isAllowed(TEST_KEY);

    // Assert
    assertThat(isAllowed).isTrue(); // Allowed because old timestamps are pruned

    // Verify the cache was updated. The new list should contain the one valid
    // old timestamp and the new one.
    verify(cache).put(eq(TEST_KEY), timestampsCaptor.capture());
    List<Long> capturedTimestamps = timestampsCaptor.getValue();
    assertThat(capturedTimestamps)
      .hasSize(2) // The two expired ones were removed, one new one was added
      .containsExactly(now - 10_000L, now);
  }

  @Test
  void isAllowed_withDifferentKeys_shouldNotInterfere() {
    // Arrange
    long now = System.currentTimeMillis();
    setupMockedTime(now);
    String key1 = "key-1";
    String key2 = "key-2";

    // Key 1 is at its limit
    List<Long> key1Timestamps = List.of(now - 1L, now - 2L, now - 3L);
    when(cache.get(key1)).thenReturn(new SimpleValueWrapper(key1Timestamps));

    // Key 2 has no requests yet
    when(cache.get(key2)).thenReturn(null);

    // Act
    boolean isAllowedForKey1 = rateLimitingService.isAllowed(key1);
    boolean isAllowedForKey2 = rateLimitingService.isAllowed(key2);

    // Assert
    assertThat(isAllowedForKey1).isFalse();
    assertThat(isAllowedForKey2).isTrue();

    // Verify cache was only updated for key2
    verify(cache, never()).put(eq(key1), any());
    verify(cache, times(1)).put(eq(key2), any());
  }
}

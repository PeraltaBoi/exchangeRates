package com.currencyexchange.ExchangeRateApi.services;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.currencyexchange.ExchangeRateApi.services.interfaces.IRateLimitingService;

@Service
public class RateLimitingService implements IRateLimitingService {

  public static final String CACHE_NAME = "rate-limit-cache";

  private final Cache cache;
  private final int limit;
  private final long windowInMillis;

  public RateLimitingService(
      CacheManager cacheManager,
      @Value("${api.rate-limiting.limit}") int limit,
      @Value("${api.rate-limiting.window-sec}") int windowInSec) {
    this.cache = cacheManager.getCache(CACHE_NAME);
    this.limit = limit;
    this.windowInMillis = windowInSec * 1000L;
  }

  public boolean isAllowed(String key) {
    synchronized (key.intern()) {
      long now = Instant.now().toEpochMilli();
      long windowStart = now - windowInMillis;

      List<Long> timestamps = getTimestamps(key);

      timestamps.removeIf(ts -> ts <= windowStart);

      if (timestamps.size() < limit) {
        timestamps.add(now);
        cache.put(key, timestamps);
        return true;
      }
    }
    return false;
  }

  private List<Long> getTimestamps(String key) {
    Cache.ValueWrapper valueWrapper = cache.get(key);
    if (valueWrapper != null && valueWrapper.get() != null) {
      Object cachedObject = valueWrapper.get();
      if (cachedObject instanceof List) {
        List<?> rawList = (List<?>) cachedObject;
        List<Long> timestamps = new LinkedList<>();
        for (Object item : rawList) {
          if (item instanceof Long) {
            timestamps.add((Long) item);
          } else {
            throw new IllegalArgumentException(
                "Cache data corruption for key '"
                    + key
                    + "': Expected elements of type Long, but found "
                    + (item != null ? item.getClass().getName() : "null")
                    + ".");
          }
        }
        return timestamps;
      } else {
        throw new IllegalArgumentException(
            "Cache data corruption for key '"
                + key
                + "': Expected List, but found "
                + (cachedObject != null
                    ? cachedObject.getClass().getName()
                    : "null")
                + ".");
      }
    }
    return new LinkedList<>();
  }
}

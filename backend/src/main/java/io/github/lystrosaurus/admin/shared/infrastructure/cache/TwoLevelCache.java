package io.github.lystrosaurus.admin.shared.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * 两级缓存：Caffeine L1（进程内）+ Redis L2（跨实例）。
 *
 * <p>Redis 不可用时 fail-open：读失败按未命中处理，写失败仅告警，业务仍使用回源结果。缓存开关关闭时直接执行 loader。
 */
@Slf4j
@Component
public class TwoLevelCache {

  private static final String KEY_PREFIX = "cache:";

  private static final long L1_MAXIMUM_SIZE = 1000;

  private final StringRedisTemplate stringRedisTemplate;

  private final ObjectMapper objectMapper;

  private final ConcurrentMap<String, Cache<String, Object>> l1Caches = new ConcurrentHashMap<>();

  /** 缓存开关，默认开启。 */
  @Value("${app.cache.enabled:true}")
  private boolean enabled;

  public TwoLevelCache(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.objectMapper = objectMapper;
  }

  /**
   * 读取缓存，未命中时回源并回填。
   *
   * @param cacheName 缓存名（同时作为 L2 key 命名空间）
   * @param key 业务键
   * @param type 值类型（支持 {@code List<...>} 等泛型）
   * @param l1Ttl L1 过期时间
   * @param l2Ttl L2 过期时间
   * @param loader 回源逻辑
   */
  public <K, V> V get(
      String cacheName,
      K key,
      TypeReference<V> type,
      Duration l1Ttl,
      Duration l2Ttl,
      Supplier<V> loader) {
    if (!enabled) {
      return loader.get();
    }

    String cacheKey = String.valueOf(key);
    Cache<String, Object> l1 =
        l1Caches.computeIfAbsent(
            cacheName,
            name ->
                Caffeine.newBuilder()
                    .maximumSize(L1_MAXIMUM_SIZE)
                    .expireAfterWrite(l1Ttl)
                    .build());

    Object l1Value = l1.getIfPresent(cacheKey);
    if (l1Value != null) {
      return cast(l1Value);
    }

    V l2Value = readFromRedis(cacheName, cacheKey, type);
    if (l2Value != null) {
      l1.put(cacheKey, l2Value);
      return l2Value;
    }

    V value = loader.get();
    if (value != null) {
      l1.put(cacheKey, value);
      writeToRedis(cacheName, cacheKey, value, l2Ttl);
    }
    return value;
  }

  /** 失效单个键。 */
  public void evict(String cacheName, Object key) {
    if (!enabled) {
      return;
    }

    String cacheKey = String.valueOf(key);
    Cache<String, Object> l1 = l1Caches.get(cacheName);
    if (l1 != null) {
      l1.invalidate(cacheKey);
    }
    try {
      stringRedisTemplate.delete(l2Key(cacheName, cacheKey));
    } catch (Exception e) {
      log.warn("Cache evict failed, fail-open: {}", e.getMessage());
    }
  }

  /** 失效整个缓存命名空间（L1 全量清空 + L2 前缀删除）。 */
  public void evictAll(String cacheName) {
    if (!enabled) {
      return;
    }

    Cache<String, Object> l1 = l1Caches.get(cacheName);
    if (l1 != null) {
      l1.invalidateAll();
    }
    try {
      Set<String> keys = stringRedisTemplate.keys(KEY_PREFIX + cacheName + ":*");
      if (keys != null && !keys.isEmpty()) {
        stringRedisTemplate.delete(keys);
      }
    } catch (Exception e) {
      log.warn("Cache evictAll failed, fail-open: {}", e.getMessage());
    }
  }

  private <V> V readFromRedis(String cacheName, String cacheKey, TypeReference<V> type) {
    try {
      String json = stringRedisTemplate.opsForValue().get(l2Key(cacheName, cacheKey));
      if (json == null || json.isBlank()) {
        return null;
      }
      return objectMapper.readValue(json, type);
    } catch (Exception e) {
      log.warn("Cache read failed, fail-open: {}", e.getMessage());
      return null;
    }
  }

  private void writeToRedis(String cacheName, String cacheKey, Object value, Duration l2Ttl) {
    try {
      stringRedisTemplate
          .opsForValue()
          .set(l2Key(cacheName, cacheKey), objectMapper.writeValueAsString(value), l2Ttl);
    } catch (Exception e) {
      log.warn("Cache write failed, fail-open: {}", e.getMessage());
    }
  }

  private String l2Key(String cacheName, String cacheKey) {
    return KEY_PREFIX + cacheName + ":" + cacheKey;
  }

  @SuppressWarnings("unchecked")
  private <V> V cast(Object value) {
    return (V) value;
  }
}

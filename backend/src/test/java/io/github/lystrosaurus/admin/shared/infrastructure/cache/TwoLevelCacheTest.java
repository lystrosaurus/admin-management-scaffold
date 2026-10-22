package io.github.lystrosaurus.admin.shared.infrastructure.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@DisplayName("两级缓存测试")
@SuppressWarnings("unchecked")
class TwoLevelCacheTest {

  private static final String CACHE_NAME = "perm:codes:user";
  private static final String L2_KEY = "cache:perm:codes:user:1";
  private static final Duration L1_TTL = Duration.ofSeconds(60);
  private static final Duration L2_TTL = Duration.ofSeconds(300);
  private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {};

  private StringRedisTemplate stringRedisTemplate;
  private ValueOperations<String, String> valueOperations;
  private TwoLevelCache cache;

  @BeforeEach
  void setUp() {
    stringRedisTemplate = mock(StringRedisTemplate.class);
    valueOperations = mock(ValueOperations.class);
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    cache = new TwoLevelCache(stringRedisTemplate, new ObjectMapper());
    ReflectionTestUtils.setField(cache, "enabled", true);
  }

  private List<String> get(Supplier<List<String>> loader) {
    return cache.get(CACHE_NAME, 1L, STRING_LIST, L1_TTL, L2_TTL, loader);
  }

  @Test
  @DisplayName("L1 命中时 loader 只执行一次")
  void should_hit_l1_and_load_once() {
    Supplier<List<String>> loader = mock(Supplier.class);
    when(loader.get()).thenReturn(List.of("a", "b"));

    assertEquals(List.of("a", "b"), get(loader));
    assertEquals(List.of("a", "b"), get(loader));

    verify(loader, times(1)).get();
    verify(valueOperations, times(1)).get(L2_KEY);
  }

  @Test
  @DisplayName("L1 未命中时从 L2 读取并回填 L1")
  void should_read_from_l2_on_l1_miss() {
    when(valueOperations.get(L2_KEY)).thenReturn("[\"a\",\"b\"]");
    Supplier<List<String>> loader = mock(Supplier.class);

    assertEquals(List.of("a", "b"), get(loader));

    verify(loader, never()).get();
    assertEquals(List.of("a", "b"), get(loader));
    verify(loader, never()).get();
  }

  @Test
  @DisplayName("L2 未命中时回源并回填 L1/L2")
  void should_load_and_backfill_on_miss() {
    Supplier<List<String>> loader = mock(Supplier.class);
    when(loader.get()).thenReturn(List.of("a"));

    assertEquals(List.of("a"), get(loader));

    verify(valueOperations).set(L2_KEY, "[\"a\"]", L2_TTL);
    assertEquals(List.of("a"), get(loader));
    verify(loader, times(1)).get();
  }

  @Test
  @DisplayName("evict 应同时清理 L1 与 L2")
  void should_evict_single_key() {
    Supplier<List<String>> loader = mock(Supplier.class);
    when(loader.get()).thenReturn(List.of("a"));

    get(loader);
    cache.evict(CACHE_NAME, 1L);

    verify(stringRedisTemplate).delete(L2_KEY);

    get(loader);
    verify(loader, times(2)).get();
  }

  @Test
  @DisplayName("evictAll 应清空 L1 并按前缀删除 L2")
  void should_evict_all_keys() {
    Supplier<List<String>> loader = mock(Supplier.class);
    when(loader.get()).thenReturn(List.of("a"));
    Set<String> keys = Set.of(L2_KEY, "cache:perm:codes:user:2");
    when(stringRedisTemplate.keys("cache:perm:codes:user:*")).thenReturn(keys);

    get(loader);
    cache.evictAll(CACHE_NAME);

    verify(stringRedisTemplate).delete(keys);

    get(loader);
    verify(loader, times(2)).get();
  }

  @Test
  @DisplayName("Redis 异常时应 fail-open 并返回回源结果")
  void should_fail_open_when_redis_unavailable() {
    when(valueOperations.get(anyString())).thenThrow(new RuntimeException("redis down"));
    doThrow(new RuntimeException("redis down"))
        .when(valueOperations)
        .set(anyString(), anyString(), any(Duration.class));
    Supplier<List<String>> loader = mock(Supplier.class);
    when(loader.get()).thenReturn(List.of("a"));

    assertEquals(List.of("a"), get(loader));
  }

  @Test
  @DisplayName("开关关闭时直接执行 loader，不访问 Redis")
  void should_bypass_cache_when_disabled() {
    ReflectionTestUtils.setField(cache, "enabled", false);
    Supplier<List<String>> loader = mock(Supplier.class);
    when(loader.get()).thenReturn(List.of("a"));

    assertEquals(List.of("a"), get(loader));

    verify(loader).get();
    verifyNoInteractions(stringRedisTemplate);
  }
}

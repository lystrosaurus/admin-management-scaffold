package io.github.lystrosaurus.admin.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.lystrosaurus.admin.BaseTest;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.infrastructure.cache.TwoLevelCache;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionQueryPort;
import io.github.lystrosaurus.admin.system.role.domain.event.RolePermissionChanged;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.TestPropertySource;

/** 权限缓存失效集成测试：使用真实 Redis 与 Spring 上下文验证事件驱动的缓存清理。 */
@DisplayName("权限缓存失效集成测试")
@TestPropertySource(properties = "app.cache.enabled=true")
class CacheInvalidationIntegrationTest extends BaseTest {

  private static final Long ADMIN_USER_ID = 1L;
  private static final String PERMISSION_CACHE = "perm:codes:user";
  private static final String PERMISSION_CACHE_KEY = "cache:perm:codes:user:1";

  @Autowired private PermissionQueryPort permissionQueryPort;

  @Autowired private DomainEventPublisher domainEventPublisher;

  @Autowired private TwoLevelCache twoLevelCache;

  @Autowired private StringRedisTemplate stringRedisTemplate;

  @BeforeEach
  void setUp() {
    evictCaches();
  }

  @AfterEach
  void cleanUp() {
    evictCaches();
    Set<String> keys = stringRedisTemplate.keys("cache:*");
    if (keys != null && !keys.isEmpty()) {
      stringRedisTemplate.delete(keys);
    }
  }

  @Test
  @DisplayName("角色权限变更事件应清理缓存并在下次查询时回源")
  void should_evict_permission_cache_after_role_permission_changed() {
    List<String> first = permissionQueryPort.findCodesByUserId(ADMIN_USER_ID);
    assertFalse(first.isEmpty(), "管理员应拥有种子权限");
    assertTrue(Boolean.TRUE.equals(stringRedisTemplate.hasKey(PERMISSION_CACHE_KEY)), "首次查询应回填 L2");

    assertEquals(first, permissionQueryPort.findCodesByUserId(ADMIN_USER_ID));

    domainEventPublisher.publish(RolePermissionChanged.of(1L, List.of()));

    assertFalse(
        Boolean.TRUE.equals(stringRedisTemplate.hasKey(PERMISSION_CACHE_KEY)), "事件后 L2 应被清理");

    List<String> reloaded = permissionQueryPort.findCodesByUserId(ADMIN_USER_ID);
    assertEquals(first, reloaded);
    assertTrue(
        Boolean.TRUE.equals(stringRedisTemplate.hasKey(PERMISSION_CACHE_KEY)), "再次查询应回源并回填");
  }

  private void evictCaches() {
    twoLevelCache.evictAll(PERMISSION_CACHE);
    twoLevelCache.evictAll("role:user");
    twoLevelCache.evictAll("menu:user");
  }
}

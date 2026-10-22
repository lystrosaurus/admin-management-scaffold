package io.github.lystrosaurus.admin.system.permission.infrastructure.acl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.shared.infrastructure.cache.TwoLevelCache;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionQueryRepository;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionSummary;
import io.github.lystrosaurus.admin.system.permission.domain.model.Permission;
import io.github.lystrosaurus.admin.system.permission.domain.model.PermissionId;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

/** PermissionQueryPortImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限查询端口实现测试")
class PermissionQueryPortImplTest {

  @Mock private PermissionQueryRepository queryRepository;
  @Mock private StringRedisTemplate stringRedisTemplate;

  private PermissionQueryPortImpl port;

  @BeforeEach
  void setUp() {
    TwoLevelCache cache = new TwoLevelCache(stringRedisTemplate, new ObjectMapper());
    ReflectionTestUtils.setField(cache, "enabled", false);
    port = new PermissionQueryPortImpl(queryRepository, cache);
  }

  private Permission permission(Long id, String code) {
    return Permission.restore(
        PermissionId.of(id),
        code,
        "权限" + id,
        "API",
        "user",
        "user",
        "read",
        null,
        1,
        "ENABLED",
        LocalDateTime.now(),
        1);
  }

  @Test
  @DisplayName("按用户查询权限编码并过滤空编码")
  void should_find_codes_by_user_id_and_filter_blank() {
    when(queryRepository.findByUserId(1L))
        .thenReturn(List.of(permission(1L, "user:read"), permission(2L, ""), permission(3L, null)));

    assertEquals(List.of("user:read"), port.findCodesByUserId(1L));
  }

  @Test
  @DisplayName("按角色查询权限摘要")
  void should_find_summaries_by_role_id() {
    when(queryRepository.findByRoleId(2L)).thenReturn(List.of(permission(1L, "user:read")));

    List<PermissionSummary> result = port.findByRoleId(2L);

    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).id());
    assertEquals("user:read", result.get(0).code());
  }

  @Test
  @DisplayName("没有权限时返回空列表")
  void should_return_empty_when_no_permissions() {
    when(queryRepository.findByUserId(1L)).thenReturn(List.of());
    when(queryRepository.findByRoleId(2L)).thenReturn(List.of());

    assertTrue(port.findCodesByUserId(1L).isEmpty());
    assertTrue(port.findByRoleId(2L).isEmpty());
  }
}

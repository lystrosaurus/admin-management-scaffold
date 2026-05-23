package io.github.lystrosaurus.admin.system.permission.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** SysPermission 实体测试 */
class SysPermissionTest {

  @Test
  void should_create_sys_permission_with_all_fields() {
    // Given
    LocalDateTime now = LocalDateTime.now();

    // When
    SysPermission permission = new SysPermission();
    permission.setId(1L);
    permission.setCode("user:create");
    permission.setName("创建用户");
    permission.setDescription("创建用户权限");
    permission.setType("BUTTON");
    permission.setModule("system");
    permission.setResource("user");
    permission.setAction("create");
    permission.setStatus("ENABLED");
    permission.setSortOrder(1);
    permission.setCreatedAt(now);
    permission.setCreatedBy("system");
    permission.setUpdatedAt(now);
    permission.setUpdatedBy("system");
    permission.setDeleted(0);
    permission.setVersion(1);

    // Then
    assertEquals(1L, permission.getId());
    assertEquals("user:create", permission.getCode());
    assertEquals("创建用户", permission.getName());
    assertEquals("创建用户权限", permission.getDescription());
    assertEquals("BUTTON", permission.getType());
    assertEquals("system", permission.getModule());
    assertEquals("user", permission.getResource());
    assertEquals("create", permission.getAction());
    assertEquals("ENABLED", permission.getStatus());
    assertEquals(1, permission.getSortOrder());
    assertEquals(now, permission.getCreatedAt());
    assertEquals("system", permission.getCreatedBy());
    assertEquals(now, permission.getUpdatedAt());
    assertEquals("system", permission.getUpdatedBy());
    assertEquals(0, permission.getDeleted());
    assertEquals(1, permission.getVersion());
  }

  @Test
  void should_set_and_get_individual_fields() {
    // Given
    SysPermission permission = new SysPermission();

    // When & Then - 逐个字段测试
    permission.setId(2L);
    assertEquals(2L, permission.getId());

    permission.setCode("user:read");
    assertEquals("user:read", permission.getCode());

    permission.setName("查看用户");
    assertEquals("查看用户", permission.getName());

    permission.setDescription("查看用户权限");
    assertEquals("查看用户权限", permission.getDescription());

    permission.setType("API");
    assertEquals("API", permission.getType());

    permission.setModule("system");
    assertEquals("system", permission.getModule());

    permission.setResource("user");
    assertEquals("user", permission.getResource());

    permission.setAction("read");
    assertEquals("read", permission.getAction());

    permission.setStatus("DISABLED");
    assertEquals("DISABLED", permission.getStatus());

    permission.setSortOrder(2);
    assertEquals(2, permission.getSortOrder());
  }

  @Test
  void should_inherit_base_entity_fields() {
    // Given
    SysPermission permission = new SysPermission();
    LocalDateTime now = LocalDateTime.now();

    // When
    permission.setCreatedAt(now);
    permission.setCreatedBy("admin");
    permission.setUpdatedAt(now);
    permission.setUpdatedBy("admin");
    permission.setDeleted(0);
    permission.setVersion(1);

    // Then
    assertEquals(now, permission.getCreatedAt());
    assertEquals("admin", permission.getCreatedBy());
    assertEquals(now, permission.getUpdatedAt());
    assertEquals("admin", permission.getUpdatedBy());
    assertEquals(0, permission.getDeleted());
    assertEquals(1, permission.getVersion());
  }
}

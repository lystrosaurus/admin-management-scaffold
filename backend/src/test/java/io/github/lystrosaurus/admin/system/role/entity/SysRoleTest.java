package io.github.lystrosaurus.admin.system.role.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** SysRole 实体测试 */
class SysRoleTest {

  @Test
  void should_create_sys_role_with_all_fields() {
    // Given
    LocalDateTime now = LocalDateTime.now();

    // When
    SysRole role = new SysRole();
    role.setId(1L);
    role.setCode("ADMIN");
    role.setName("管理员");
    role.setDescription("系统管理员角色");
    role.setSortOrder(1);
    role.setStatus("ENABLED");
    role.setDataScopeType("ALL");
    role.setCreatedAt(now);
    role.setCreatedBy("system");
    role.setUpdatedAt(now);
    role.setUpdatedBy("system");
    role.setDeleted(0);
    role.setVersion(1);

    // Then
    assertEquals(1L, role.getId());
    assertEquals("ADMIN", role.getCode());
    assertEquals("管理员", role.getName());
    assertEquals("系统管理员角色", role.getDescription());
    assertEquals(1, role.getSortOrder());
    assertEquals("ENABLED", role.getStatus());
    assertEquals("ALL", role.getDataScopeType());
    assertEquals(now, role.getCreatedAt());
    assertEquals("system", role.getCreatedBy());
    assertEquals(now, role.getUpdatedAt());
    assertEquals("system", role.getUpdatedBy());
    assertEquals(0, role.getDeleted());
    assertEquals(1, role.getVersion());
  }

  @Test
  void should_set_and_get_individual_fields() {
    // Given
    SysRole role = new SysRole();

    // When & Then - 逐个字段测试
    role.setId(2L);
    assertEquals(2L, role.getId());

    role.setCode("USER");
    assertEquals("USER", role.getCode());

    role.setName("普通用户");
    assertEquals("普通用户", role.getName());

    role.setDescription("普通用户角色");
    assertEquals("普通用户角色", role.getDescription());

    role.setSortOrder(2);
    assertEquals(2, role.getSortOrder());

    role.setStatus("DISABLED");
    assertEquals("DISABLED", role.getStatus());

    role.setDataScopeType("ORG_TREE");
    assertEquals("ORG_TREE", role.getDataScopeType());
  }

  @Test
  void should_inherit_base_entity_fields() {
    // Given
    SysRole role = new SysRole();
    LocalDateTime now = LocalDateTime.now();

    // When
    role.setCreatedAt(now);
    role.setCreatedBy("admin");
    role.setUpdatedAt(now);
    role.setUpdatedBy("admin");
    role.setDeleted(0);
    role.setVersion(1);

    // Then
    assertEquals(now, role.getCreatedAt());
    assertEquals("admin", role.getCreatedBy());
    assertEquals(now, role.getUpdatedAt());
    assertEquals("admin", role.getUpdatedBy());
    assertEquals(0, role.getDeleted());
    assertEquals(1, role.getVersion());
  }
}

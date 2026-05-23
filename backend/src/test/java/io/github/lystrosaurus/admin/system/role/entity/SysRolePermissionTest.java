package io.github.lystrosaurus.admin.system.role.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** SysRolePermission 实体测试 */
class SysRolePermissionTest {

  @Test
  void should_create_sys_role_permission_with_all_fields() {
    // Given
    LocalDateTime now = LocalDateTime.now();

    // When
    SysRolePermission rolePermission = new SysRolePermission();
    rolePermission.setId(1L);
    rolePermission.setRoleId(100L);
    rolePermission.setPermissionId(200L);
    rolePermission.setCreatedAt(now);
    rolePermission.setCreatedBy("system");
    rolePermission.setUpdatedAt(now);
    rolePermission.setUpdatedBy("system");
    rolePermission.setDeleted(0);
    rolePermission.setVersion(1);

    // Then
    assertEquals(1L, rolePermission.getId());
    assertEquals(100L, rolePermission.getRoleId());
    assertEquals(200L, rolePermission.getPermissionId());
    assertEquals(now, rolePermission.getCreatedAt());
    assertEquals("system", rolePermission.getCreatedBy());
    assertEquals(now, rolePermission.getUpdatedAt());
    assertEquals("system", rolePermission.getUpdatedBy());
    assertEquals(0, rolePermission.getDeleted());
    assertEquals(1, rolePermission.getVersion());
  }

  @Test
  void should_set_and_get_individual_fields() {
    // Given
    SysRolePermission rolePermission = new SysRolePermission();

    // When & Then - 逐个字段测试
    rolePermission.setId(2L);
    assertEquals(2L, rolePermission.getId());

    rolePermission.setRoleId(300L);
    assertEquals(300L, rolePermission.getRoleId());

    rolePermission.setPermissionId(400L);
    assertEquals(400L, rolePermission.getPermissionId());
  }

  @Test
  void should_inherit_base_entity_fields() {
    // Given
    SysRolePermission rolePermission = new SysRolePermission();
    LocalDateTime now = LocalDateTime.now();

    // When
    rolePermission.setCreatedAt(now);
    rolePermission.setCreatedBy("admin");
    rolePermission.setUpdatedAt(now);
    rolePermission.setUpdatedBy("admin");
    rolePermission.setDeleted(0);
    rolePermission.setVersion(1);

    // Then
    assertEquals(now, rolePermission.getCreatedAt());
    assertEquals("admin", rolePermission.getCreatedBy());
    assertEquals(now, rolePermission.getUpdatedAt());
    assertEquals("admin", rolePermission.getUpdatedBy());
    assertEquals(0, rolePermission.getDeleted());
    assertEquals(1, rolePermission.getVersion());
  }
}

package io.github.lystrosaurus.admin.system.user.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** SysUserRole 实体测试 */
class SysUserRoleTest {

  @Test
  void should_create_sys_user_role_with_all_fields() {
    // Given
    LocalDateTime now = LocalDateTime.now();

    // When
    SysUserRole userRole = new SysUserRole();
    userRole.setId(1L);
    userRole.setUserId(100L);
    userRole.setRoleId(200L);
    userRole.setCreatedAt(now);
    userRole.setCreatedBy("system");
    userRole.setUpdatedAt(now);
    userRole.setUpdatedBy("system");
    userRole.setDeleted(0);
    userRole.setVersion(1);

    // Then
    assertEquals(1L, userRole.getId());
    assertEquals(100L, userRole.getUserId());
    assertEquals(200L, userRole.getRoleId());
    assertEquals(now, userRole.getCreatedAt());
    assertEquals("system", userRole.getCreatedBy());
    assertEquals(now, userRole.getUpdatedAt());
    assertEquals("system", userRole.getUpdatedBy());
    assertEquals(0, userRole.getDeleted());
    assertEquals(1, userRole.getVersion());
  }

  @Test
  void should_set_and_get_individual_fields() {
    // Given
    SysUserRole userRole = new SysUserRole();

    // When & Then - 逐个字段测试
    userRole.setId(2L);
    assertEquals(2L, userRole.getId());

    userRole.setUserId(300L);
    assertEquals(300L, userRole.getUserId());

    userRole.setRoleId(400L);
    assertEquals(400L, userRole.getRoleId());
  }

  @Test
  void should_inherit_base_entity_fields() {
    // Given
    SysUserRole userRole = new SysUserRole();
    LocalDateTime now = LocalDateTime.now();

    // When
    userRole.setCreatedAt(now);
    userRole.setCreatedBy("admin");
    userRole.setUpdatedAt(now);
    userRole.setUpdatedBy("admin");
    userRole.setDeleted(0);
    userRole.setVersion(1);

    // Then
    assertEquals(now, userRole.getCreatedAt());
    assertEquals("admin", userRole.getCreatedBy());
    assertEquals(now, userRole.getUpdatedAt());
    assertEquals("admin", userRole.getUpdatedBy());
    assertEquals(0, userRole.getDeleted());
    assertEquals(1, userRole.getVersion());
  }
}

package io.github.lystrosaurus.admin.system.role.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** SysRoleMenu 实体测试 */
class SysRoleMenuTest {

  @Test
  void should_create_sys_role_menu_with_all_fields() {
    // Given
    LocalDateTime now = LocalDateTime.now();

    // When
    SysRoleMenu roleMenu = new SysRoleMenu();
    roleMenu.setId(1L);
    roleMenu.setRoleId(100L);
    roleMenu.setMenuId(200L);
    roleMenu.setCreatedAt(now);
    roleMenu.setCreatedBy("system");
    roleMenu.setUpdatedAt(now);
    roleMenu.setUpdatedBy("system");
    roleMenu.setDeleted(0);
    roleMenu.setVersion(1);

    // Then
    assertEquals(1L, roleMenu.getId());
    assertEquals(100L, roleMenu.getRoleId());
    assertEquals(200L, roleMenu.getMenuId());
    assertEquals(now, roleMenu.getCreatedAt());
    assertEquals("system", roleMenu.getCreatedBy());
    assertEquals(now, roleMenu.getUpdatedAt());
    assertEquals("system", roleMenu.getUpdatedBy());
    assertEquals(0, roleMenu.getDeleted());
    assertEquals(1, roleMenu.getVersion());
  }

  @Test
  void should_set_and_get_individual_fields() {
    // Given
    SysRoleMenu roleMenu = new SysRoleMenu();

    // When & Then - 逐个字段测试
    roleMenu.setId(2L);
    assertEquals(2L, roleMenu.getId());

    roleMenu.setRoleId(300L);
    assertEquals(300L, roleMenu.getRoleId());

    roleMenu.setMenuId(400L);
    assertEquals(400L, roleMenu.getMenuId());
  }

  @Test
  void should_inherit_base_entity_fields() {
    // Given
    SysRoleMenu roleMenu = new SysRoleMenu();
    LocalDateTime now = LocalDateTime.now();

    // When
    roleMenu.setCreatedAt(now);
    roleMenu.setCreatedBy("admin");
    roleMenu.setUpdatedAt(now);
    roleMenu.setUpdatedBy("admin");
    roleMenu.setDeleted(0);
    roleMenu.setVersion(1);

    // Then
    assertEquals(now, roleMenu.getCreatedAt());
    assertEquals("admin", roleMenu.getCreatedBy());
    assertEquals(now, roleMenu.getUpdatedAt());
    assertEquals("admin", roleMenu.getUpdatedBy());
    assertEquals(0, roleMenu.getDeleted());
    assertEquals(1, roleMenu.getVersion());
  }
}

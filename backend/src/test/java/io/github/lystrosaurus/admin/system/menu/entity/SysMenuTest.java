package io.github.lystrosaurus.admin.system.menu.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** SysMenu 实体测试 */
class SysMenuTest {

  @Test
  void should_create_sys_menu_with_all_fields() {
    // Given
    LocalDateTime now = LocalDateTime.now();

    // When
    SysMenu menu = new SysMenu();
    menu.setId(1L);
    menu.setParentId(0L);
    menu.setName("系统管理");
    menu.setPath("/system");
    menu.setComponent("Layout");
    menu.setIcon("setting");
    menu.setSortOrder(1);
    menu.setType((byte) 1);
    menu.setPermissionCode("system:manage");
    menu.setVisible((byte) 1);
    menu.setStatus((byte) 1);
    menu.setVersion(1);
    menu.setCreatedAt(now);
    menu.setCreatedBy("system");
    menu.setUpdatedAt(now);
    menu.setUpdatedBy("system");
    menu.setDeleted(0);

    // Then
    assertEquals(1L, menu.getId());
    assertEquals(0L, menu.getParentId());
    assertEquals("系统管理", menu.getName());
    assertEquals("/system", menu.getPath());
    assertEquals("Layout", menu.getComponent());
    assertEquals("setting", menu.getIcon());
    assertEquals(1, menu.getSortOrder());
    assertEquals((byte) 1, menu.getType());
    assertEquals("system:manage", menu.getPermissionCode());
    assertEquals((byte) 1, menu.getVisible());
    assertEquals((byte) 1, menu.getStatus());
    assertEquals(1, menu.getVersion());
    assertEquals(now, menu.getCreatedAt());
    assertEquals("system", menu.getCreatedBy());
    assertEquals(now, menu.getUpdatedAt());
    assertEquals("system", menu.getUpdatedBy());
    assertEquals(0, menu.getDeleted());
  }

  @Test
  void should_set_and_get_individual_fields() {
    // Given
    SysMenu menu = new SysMenu();

    // When & Then - 逐个字段测试
    menu.setId(2L);
    assertEquals(2L, menu.getId());

    menu.setParentId(1L);
    assertEquals(1L, menu.getParentId());

    menu.setName("用户管理");
    assertEquals("用户管理", menu.getName());

    menu.setPath("/system/user");
    assertEquals("/system/user", menu.getPath());

    menu.setComponent("system/user/index");
    assertEquals("system/user/index", menu.getComponent());

    menu.setIcon("user");
    assertEquals("user", menu.getIcon());

    menu.setSortOrder(1);
    assertEquals(1, menu.getSortOrder());

    menu.setType((byte) 2);
    assertEquals((byte) 2, menu.getType());

    menu.setPermissionCode("system:user:list");
    assertEquals("system:user:list", menu.getPermissionCode());

    menu.setVisible((byte) 1);
    assertEquals((byte) 1, menu.getVisible());

    menu.setStatus((byte) 0);
    assertEquals((byte) 0, menu.getStatus());

    menu.setVersion(2);
    assertEquals(2, menu.getVersion());
  }

  @Test
  void should_inherit_base_entity_fields() {
    // Given
    SysMenu menu = new SysMenu();
    LocalDateTime now = LocalDateTime.now();

    // When
    menu.setCreatedAt(now);
    menu.setCreatedBy("admin");
    menu.setUpdatedAt(now);
    menu.setUpdatedBy("admin");
    menu.setDeleted(0);
    menu.setVersion(1);

    // Then
    assertEquals(now, menu.getCreatedAt());
    assertEquals("admin", menu.getCreatedBy());
    assertEquals(now, menu.getUpdatedAt());
    assertEquals("admin", menu.getUpdatedBy());
    assertEquals(0, menu.getDeleted());
    assertEquals(1, menu.getVersion());
  }
}

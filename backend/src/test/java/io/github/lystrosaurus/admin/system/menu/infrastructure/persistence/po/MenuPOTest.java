package io.github.lystrosaurus.admin.system.menu.infrastructure.persistence.po;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** MenuPO 持久化对象测试 */
class MenuPOTest {

  @Test
  void should_create_menu_po_with_all_fields() {
    LocalDateTime now = LocalDateTime.now();

    MenuPO po = new MenuPO();
    po.setId(1L);
    po.setParentId(0L);
    po.setName("系统管理");
    po.setPath("/system");
    po.setComponent("System");
    po.setIcon("setting");
    po.setSortOrder(1);
    po.setType((byte) 1);
    po.setPermissionCode("system:menu:view");
    po.setVisible((byte) 1);
    po.setStatus((byte) 1);
    po.setCreatedAt(now);
    po.setCreatedBy("system");
    po.setUpdatedAt(now);
    po.setUpdatedBy("system");
    po.setDeleted(0);
    po.setVersion(1);

    assertEquals(1L, po.getId());
    assertEquals(0L, po.getParentId());
    assertEquals("系统管理", po.getName());
    assertEquals("/system", po.getPath());
    assertEquals("System", po.getComponent());
    assertEquals("setting", po.getIcon());
    assertEquals(1, po.getSortOrder());
    assertEquals((byte) 1, po.getType());
    assertEquals("system:menu:view", po.getPermissionCode());
    assertEquals((byte) 1, po.getVisible());
    assertEquals((byte) 1, po.getStatus());
    assertEquals(now, po.getCreatedAt());
    assertEquals("system", po.getCreatedBy());
    assertEquals(0, po.getDeleted());
    assertEquals(1, po.getVersion());
  }

  @Test
  void should_set_and_get_individual_fields() {
    MenuPO po = new MenuPO();

    po.setId(2L);
    assertEquals(2L, po.getId());

    po.setParentId(1L);
    assertEquals(1L, po.getParentId());

    po.setName("用户管理");
    assertEquals("用户管理", po.getName());

    po.setVisible((byte) 0);
    assertEquals((byte) 0, po.getVisible());
  }

  @Test
  void should_inherit_base_po_fields() {
    MenuPO po = new MenuPO();
    LocalDateTime now = LocalDateTime.now();

    po.setCreatedAt(now);
    po.setCreatedBy("admin");
    po.setUpdatedAt(now);
    po.setUpdatedBy("admin");
    po.setDeleted(0);
    po.setVersion(1);

    assertEquals(now, po.getCreatedAt());
    assertEquals("admin", po.getCreatedBy());
    assertEquals(now, po.getUpdatedAt());
    assertEquals("admin", po.getUpdatedBy());
    assertEquals(0, po.getDeleted());
    assertEquals(1, po.getVersion());
  }
}

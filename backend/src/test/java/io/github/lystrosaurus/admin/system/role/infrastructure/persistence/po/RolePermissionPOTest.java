package io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** RolePermissionPO 持久化对象测试 */
class RolePermissionPOTest {

  @Test
  void should_create_role_permission_po_with_all_fields() {
    LocalDateTime now = LocalDateTime.now();

    RolePermissionPO po = new RolePermissionPO();
    po.setId(1L);
    po.setRoleId(100L);
    po.setPermissionId(200L);
    po.setCreatedAt(now);
    po.setCreatedBy("system");
    po.setUpdatedAt(now);
    po.setUpdatedBy("system");
    po.setDeleted(0);
    po.setVersion(1);

    assertEquals(1L, po.getId());
    assertEquals(100L, po.getRoleId());
    assertEquals(200L, po.getPermissionId());
    assertEquals(now, po.getCreatedAt());
    assertEquals("system", po.getCreatedBy());
    assertEquals(now, po.getUpdatedAt());
    assertEquals("system", po.getUpdatedBy());
    assertEquals(0, po.getDeleted());
    assertEquals(1, po.getVersion());
  }

  @Test
  void should_set_and_get_individual_fields() {
    RolePermissionPO po = new RolePermissionPO();

    po.setId(2L);
    assertEquals(2L, po.getId());

    po.setRoleId(300L);
    assertEquals(300L, po.getRoleId());

    po.setPermissionId(400L);
    assertEquals(400L, po.getPermissionId());
  }

  @Test
  void should_inherit_base_po_fields() {
    RolePermissionPO po = new RolePermissionPO();
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

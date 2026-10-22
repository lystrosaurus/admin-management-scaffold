package io.github.lystrosaurus.admin.system.permission.infrastructure.persistence.po;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** PermissionPO 持久化对象测试 */
class PermissionPOTest {

  @Test
  void should_create_permission_po_with_all_fields() {
    LocalDateTime now = LocalDateTime.now();

    PermissionPO po = new PermissionPO();
    po.setId(1L);
    po.setCode("user:read");
    po.setName("查看用户");
    po.setType("API");
    po.setModule("user");
    po.setResource("user");
    po.setAction("read");
    po.setDescription("查看用户权限");
    po.setSortOrder(1);
    po.setStatus("ENABLED");
    po.setCreatedAt(now);
    po.setCreatedBy("system");
    po.setUpdatedAt(now);
    po.setUpdatedBy("system");
    po.setDeleted(0);
    po.setVersion(1);

    assertEquals(1L, po.getId());
    assertEquals("user:read", po.getCode());
    assertEquals("查看用户", po.getName());
    assertEquals("API", po.getType());
    assertEquals("user", po.getModule());
    assertEquals("user", po.getResource());
    assertEquals("read", po.getAction());
    assertEquals("查看用户权限", po.getDescription());
    assertEquals(1, po.getSortOrder());
    assertEquals("ENABLED", po.getStatus());
    assertEquals(now, po.getCreatedAt());
    assertEquals("system", po.getCreatedBy());
    assertEquals(0, po.getDeleted());
    assertEquals(1, po.getVersion());
  }

  @Test
  void should_set_and_get_individual_fields() {
    PermissionPO po = new PermissionPO();

    po.setId(2L);
    assertEquals(2L, po.getId());

    po.setCode("role:delete");
    assertEquals("role:delete", po.getCode());

    po.setName("删除角色");
    assertEquals("删除角色", po.getName());

    po.setType("API");
    assertEquals("API", po.getType());
  }

  @Test
  void should_inherit_base_po_fields() {
    PermissionPO po = new PermissionPO();
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

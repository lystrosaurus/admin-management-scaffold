package io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** RolePO 持久化对象测试 */
class RolePOTest {

  @Test
  void should_create_role_po_with_all_fields() {
    LocalDateTime now = LocalDateTime.now();

    RolePO po = new RolePO();
    po.setId(1L);
    po.setCode("ADMIN");
    po.setName("管理员");
    po.setDescription("系统管理员角色");
    po.setSortOrder(1);
    po.setStatus("ENABLED");
    po.setDataScopeType("ALL");
    po.setCreatedAt(now);
    po.setCreatedBy("system");
    po.setUpdatedAt(now);
    po.setUpdatedBy("system");
    po.setDeleted(0);
    po.setVersion(1);

    assertEquals(1L, po.getId());
    assertEquals("ADMIN", po.getCode());
    assertEquals("管理员", po.getName());
    assertEquals("系统管理员角色", po.getDescription());
    assertEquals(1, po.getSortOrder());
    assertEquals("ENABLED", po.getStatus());
    assertEquals("ALL", po.getDataScopeType());
    assertEquals(now, po.getCreatedAt());
    assertEquals("system", po.getCreatedBy());
    assertEquals(now, po.getUpdatedAt());
    assertEquals("system", po.getUpdatedBy());
    assertEquals(0, po.getDeleted());
    assertEquals(1, po.getVersion());
  }

  @Test
  void should_set_and_get_individual_fields() {
    RolePO po = new RolePO();

    po.setId(2L);
    assertEquals(2L, po.getId());

    po.setCode("USER");
    assertEquals("USER", po.getCode());

    po.setName("普通用户");
    assertEquals("普通用户", po.getName());

    po.setDescription("普通用户角色");
    assertEquals("普通用户角色", po.getDescription());

    po.setSortOrder(2);
    assertEquals(2, po.getSortOrder());

    po.setStatus("DISABLED");
    assertEquals("DISABLED", po.getStatus());

    po.setDataScopeType("ORG_TREE");
    assertEquals("ORG_TREE", po.getDataScopeType());
  }

  @Test
  void should_inherit_base_po_fields() {
    RolePO po = new RolePO();
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

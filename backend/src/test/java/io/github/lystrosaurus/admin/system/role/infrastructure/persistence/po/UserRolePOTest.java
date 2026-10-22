package io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** UserRolePO 持久化对象测试 */
class UserRolePOTest {

  @Test
  void should_create_user_role_po_with_all_fields() {
    LocalDateTime now = LocalDateTime.now();

    UserRolePO po = new UserRolePO();
    po.setId(1L);
    po.setUserId(100L);
    po.setRoleId(200L);
    po.setCreatedAt(now);
    po.setCreatedBy("system");
    po.setUpdatedAt(now);
    po.setUpdatedBy("system");
    po.setDeleted(0);
    po.setVersion(1);

    assertEquals(1L, po.getId());
    assertEquals(100L, po.getUserId());
    assertEquals(200L, po.getRoleId());
    assertEquals(now, po.getCreatedAt());
    assertEquals("system", po.getCreatedBy());
    assertEquals(now, po.getUpdatedAt());
    assertEquals("system", po.getUpdatedBy());
    assertEquals(0, po.getDeleted());
    assertEquals(1, po.getVersion());
  }

  @Test
  void should_set_and_get_individual_fields() {
    UserRolePO po = new UserRolePO();

    po.setId(2L);
    assertEquals(2L, po.getId());

    po.setUserId(300L);
    assertEquals(300L, po.getUserId());

    po.setRoleId(400L);
    assertEquals(400L, po.getRoleId());
  }

  @Test
  void should_inherit_base_po_fields() {
    UserRolePO po = new UserRolePO();
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

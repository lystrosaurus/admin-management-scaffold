package io.github.lystrosaurus.admin.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** BaseEntity 单元测试 */
class BaseEntityTest {

  @Test
  void should_create_base_entity_with_default_values() {
    BaseEntity entity = new BaseEntity() {};

    assertNull(entity.getId());
    assertNull(entity.getCreateTime());
    assertNull(entity.getUpdateTime());
    assertNull(entity.getCreateBy());
    assertNull(entity.getUpdateBy());
    assertNull(entity.getDeleted());
    assertNull(entity.getVersion());
  }

  @Test
  void should_set_and_get_values_correctly() {
    BaseEntity entity = new BaseEntity() {};
    Long id = 1L;
    LocalDateTime now = LocalDateTime.now();
    String createBy = "testUser";
    String updateBy = "testUser";
    Integer deleted = 0;
    Integer version = 1;

    entity.setId(id);
    entity.setCreateTime(now);
    entity.setUpdateTime(now);
    entity.setCreateBy(createBy);
    entity.setUpdateBy(updateBy);
    entity.setDeleted(deleted);
    entity.setVersion(version);

    assertEquals(id, entity.getId());
    assertEquals(now, entity.getCreateTime());
    assertEquals(now, entity.getUpdateTime());
    assertEquals(createBy, entity.getCreateBy());
    assertEquals(updateBy, entity.getUpdateBy());
    assertEquals(deleted, entity.getDeleted());
    assertEquals(version, entity.getVersion());
  }

  @Test
  void should_implement_serializable() {
    BaseEntity entity = new BaseEntity() {};
    assertTrue(entity instanceof java.io.Serializable);
  }
}

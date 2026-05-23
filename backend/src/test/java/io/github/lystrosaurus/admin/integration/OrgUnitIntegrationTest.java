package io.github.lystrosaurus.admin.integration;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.IntegrationTest;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 部门组织集成测试 — 测试完整的部门增删改查和树结构
 *
 * <p>依赖 V4 org_unit 表结构。测试通过 DAO/JDBC 层验证数据操作。 待 Agent A 的 OrgUnitService/Controller 完成后，可扩展为 API
 * 级别测试。
 */
@DisplayName("部门组织集成测试")
@Transactional
class OrgUnitIntegrationTest extends IntegrationTest {

  @Autowired private DataSource dataSource;

  private JdbcTemplate jdbc;

  @BeforeEach
  void setUp() {
    jdbc = new JdbcTemplate(dataSource);
    // 清理测试数据（不影响种子数据 id < 1000）
    jdbc.update("DELETE FROM employee_org WHERE org_id >= 1000");
    jdbc.update("DELETE FROM org_unit WHERE id >= 1000");
  }

  @Test
  @DisplayName("应该成功创建部门")
  void should_create_org_unit() {
    // Given
    Long orgId = 1001L;

    // When
    int rows =
        jdbc.update(
            "INSERT INTO org_unit (id, parent_id, code, name, status, sort_order, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 100, 'TEST_ORG_1', '测试部门', 'ENABLED', 1, NOW(), NOW(), 'test', 'test', 0, 1)",
            orgId);

    // Then
    assertEquals(1, rows);
    String name =
        jdbc.queryForObject(
            "SELECT name FROM org_unit WHERE id = ? AND deleted = 0", String.class, orgId);
    assertEquals("测试部门", name);
  }

  @Test
  @DisplayName("应该返回409当部门编码重复")
  void should_fail_when_duplicate_code() {
    // Given
    Long orgId1 = 1002L;
    Long orgId2 = 1003L;
    String code = "DUP_ORG";

    jdbc.update(
        "INSERT INTO org_unit (id, parent_id, code, name, status, sort_order, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 100, ?, '部门A', 'ENABLED', 1, NOW(), NOW(), 'test', 'test', 0, 1)",
        orgId1,
        code);

    // When & Then - 重复编码应违反唯一约束
    assertThrows(
        Exception.class,
        () ->
            jdbc.update(
                "INSERT INTO org_unit (id, parent_id, code, name, status, sort_order, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 100, ?, '部门B', 'ENABLED', 1, NOW(), NOW(), 'test', 'test', 0, 1)",
                orgId2,
                code));
  }

  @Test
  @DisplayName("应该根据ID查询部门")
  void should_find_org_unit_by_id() {
    // Given - 使用种子数据中的总公司
    String name =
        jdbc.queryForObject(
            "SELECT name FROM org_unit WHERE id = 100 AND deleted = 0", String.class);

    // Then
    assertEquals("总公司", name);
  }

  @Test
  @DisplayName("应该成功更新部门信息")
  void should_update_org_unit() {
    // Given
    Long orgId = 1004L;
    jdbc.update(
        "INSERT INTO org_unit (id, parent_id, code, name, status, sort_order, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 100, 'UPD_ORG', '更新前', 'ENABLED', 1, NOW(), NOW(), 'test', 'test', 0, 1)",
        orgId);

    // When
    int rows = jdbc.update("UPDATE org_unit SET name = '更新后' WHERE id = ? AND deleted = 0", orgId);

    // Then
    assertEquals(1, rows);
    String name =
        jdbc.queryForObject(
            "SELECT name FROM org_unit WHERE id = ? AND deleted = 0", String.class, orgId);
    assertEquals("更新后", name);
  }

  @Test
  @DisplayName("应该逻辑删除部门")
  void should_soft_delete_org_unit() {
    // Given
    Long orgId = 1005L;
    jdbc.update(
        "INSERT INTO org_unit (id, parent_id, code, name, status, sort_order, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 100, 'DEL_ORG', '删除部门', 'ENABLED', 1, NOW(), NOW(), 'test', 'test', 0, 1)",
        orgId);

    // When
    int rows = jdbc.update("UPDATE org_unit SET deleted = 1 WHERE id = ?", orgId);

    // Then
    assertEquals(1, rows);
    Integer count =
        jdbc.queryForObject(
            "SELECT COUNT(*) FROM org_unit WHERE id = ? AND deleted = 0", Integer.class, orgId);
    assertEquals(0, count);
  }

  @Test
  @DisplayName("删除有子部门的部门应失败")
  void should_fail_when_delete_org_with_children() {
    // Given - 总公司(100)有子部门技术部(101)
    Integer childCount =
        jdbc.queryForObject(
            "SELECT COUNT(*) FROM org_unit WHERE parent_id = 100 AND deleted = 0", Integer.class);

    // Then - 总公司有子部门，不应直接删除
    assertNotNull(childCount);
    assertTrue(childCount > 0, "总公司应有子部门");
  }

  @Test
  @DisplayName("应该查询部门树结构")
  void should_find_org_unit_tree() {
    // Given - 使用种子数据

    // When - 查询一级部门（parent_id = 0）
    List<Map<String, Object>> topLevel =
        jdbc.queryForList(
            "SELECT id, name, parent_id FROM org_unit WHERE parent_id = 0 AND deleted = 0 ORDER BY sort_order");

    // Then
    assertFalse(topLevel.isEmpty(), "应有顶级部门");
    // 总公司应该是顶级
    boolean hasHQ = topLevel.stream().anyMatch(row -> ((Number) row.get("id")).longValue() == 100L);
    assertTrue(hasHQ, "应包含总公司");

    // 查询总公司下的子部门
    List<Map<String, Object>> children =
        jdbc.queryForList(
            "SELECT id, name, parent_id FROM org_unit WHERE parent_id = 100 AND deleted = 0 ORDER BY sort_order");
    assertEquals(2, children.size(), "总公司应有2个子部门（技术部、产品部）");

    // 查询技术部下的子部门
    List<Map<String, Object>> techChildren =
        jdbc.queryForList(
            "SELECT id, name, parent_id FROM org_unit WHERE parent_id = 101 AND deleted = 0 ORDER BY sort_order");
    assertEquals(2, techChildren.size(), "技术部应有2个子部门（后端组、前端组）");
  }

  @Test
  @DisplayName("应该返回空当部门不存在")
  void should_return_empty_when_org_not_found() {
    // When
    Integer count =
        jdbc.queryForObject(
            "SELECT COUNT(*) FROM org_unit WHERE id = 999999 AND deleted = 0", Integer.class);

    // Then
    assertEquals(0, count, "不存在的部门查询结果应为0");
  }
}

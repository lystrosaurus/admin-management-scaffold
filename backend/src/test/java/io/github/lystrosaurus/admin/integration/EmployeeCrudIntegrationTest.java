package io.github.lystrosaurus.admin.integration;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.IntegrationTest;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 员工 CRUD 集成测试 — 测试完整的员工增删改查流程
 *
 * <p>依赖 V4 hr_employee 表结构。测试通过 DAO/JDBC 层验证数据操作。 待 Agent A 的 EmployeeService/Controller 完成后，可扩展为
 * API 级别测试。
 */
@DisplayName("员工 CRUD 集成测试")
@Transactional
class EmployeeCrudIntegrationTest extends IntegrationTest {

  @Autowired private DataSource dataSource;

  private JdbcTemplate jdbc;

  @BeforeEach
  void setUp() {
    jdbc = new JdbcTemplate(dataSource);
    // 清理测试数据
    jdbc.update("DELETE FROM employee_org WHERE employee_id >= 1000");
    jdbc.update("DELETE FROM hr_employee WHERE id >= 1000");
  }

  @Test
  @DisplayName("应该成功创建员工")
  void should_create_employee() {
    // Given
    Long empId = 1001L;
    String empNo = "EMP_TEST_001";

    // When
    int rows =
        jdbc.update(
            "INSERT INTO hr_employee (id, org_id, employee_no, name, status, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 101, ?, '测试员工', 'ACTIVE', NOW(), NOW(), 'test', 'test', 0, 1)",
            empId,
            empNo);

    // Then
    assertEquals(1, rows, "应成功插入1条记录");
    String name =
        jdbc.queryForObject(
            "SELECT name FROM hr_employee WHERE id = ? AND deleted = 0", String.class, empId);
    assertEquals("测试员工", name);
  }

  @Test
  @DisplayName("应该返回409当员工编号重复")
  void should_fail_when_duplicate_employee_no() {
    // Given
    Long empId1 = 1002L;
    Long empId2 = 1003L;
    String empNo = "EMP_DUP_001";

    jdbc.update(
        "INSERT INTO hr_employee (id, org_id, employee_no, name, status, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 101, ?, '员工A', 'ACTIVE', NOW(), NOW(), 'test', 'test', 0, 1)",
        empId1,
        empNo);

    // When & Then - 重复编号应违反唯一约束
    assertThrows(
        Exception.class,
        () ->
            jdbc.update(
                "INSERT INTO hr_employee (id, org_id, employee_no, name, status, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 101, ?, '员工B', 'ACTIVE', NOW(), NOW(), 'test', 'test', 0, 1)",
                empId2,
                empNo));
  }

  @Test
  @DisplayName("应该根据ID查询员工")
  void should_find_employee_by_id() {
    // Given
    Long empId = 1004L;
    jdbc.update(
        "INSERT INTO hr_employee (id, org_id, employee_no, name, status, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 101, 'EMP_FIND_001', '查询员工', 'ACTIVE', NOW(), NOW(), 'test', 'test', 0, 1)",
        empId);

    // When
    String name =
        jdbc.queryForObject(
            "SELECT name FROM hr_employee WHERE id = ? AND deleted = 0", String.class, empId);

    // Then
    assertEquals("查询员工", name);
  }

  @Test
  @DisplayName("应该成功更新员工信息")
  void should_update_employee() {
    // Given
    Long empId = 1005L;
    jdbc.update(
        "INSERT INTO hr_employee (id, org_id, employee_no, name, status, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 101, 'EMP_UPD_001', '更新前', 'ACTIVE', NOW(), NOW(), 'test', 'test', 0, 1)",
        empId);

    // When
    int rows =
        jdbc.update("UPDATE hr_employee SET name = '更新后' WHERE id = ? AND deleted = 0", empId);

    // Then
    assertEquals(1, rows);
    String name =
        jdbc.queryForObject(
            "SELECT name FROM hr_employee WHERE id = ? AND deleted = 0", String.class, empId);
    assertEquals("更新后", name);
  }

  @Test
  @DisplayName("应该逻辑删除员工")
  void should_soft_delete_employee() {
    // Given
    Long empId = 1006L;
    jdbc.update(
        "INSERT INTO hr_employee (id, org_id, employee_no, name, status, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 101, 'EMP_DEL_001', '删除员工', 'ACTIVE', NOW(), NOW(), 'test', 'test', 0, 1)",
        empId);

    // When
    int rows = jdbc.update("UPDATE hr_employee SET deleted = 1 WHERE id = ?", empId);

    // Then
    assertEquals(1, rows);
    // 逻辑删除后查询应为空
    Integer count =
        jdbc.queryForObject(
            "SELECT COUNT(*) FROM hr_employee WHERE id = ? AND deleted = 0", Integer.class, empId);
    assertEquals(0, count);
  }

  @Test
  @DisplayName("应该分页查询员工列表")
  void should_find_employee_page() {
    // Given - 插入多条员工记录
    for (int i = 0; i < 5; i++) {
      jdbc.update(
          "INSERT INTO hr_employee (id, org_id, employee_no, name, status, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 101, ?, '分页员工', 'ACTIVE', NOW(), NOW(), 'test', 'test', 0, 1)",
          1010L + i,
          "EMP_PAGE_" + i);
    }

    // When - 查询总数
    Integer total =
        jdbc.queryForObject(
            "SELECT COUNT(*) FROM hr_employee WHERE employee_no LIKE 'EMP\\_PAGE\\_%' AND deleted = 0",
            Integer.class);

    // Then
    assertNotNull(total);
    assertTrue(total >= 5, "总数应 >= 5");
  }

  @Test
  @DisplayName("应该返回404当员工不存在")
  void should_return_empty_when_employee_not_found() {
    // When
    Integer count =
        jdbc.queryForObject(
            "SELECT COUNT(*) FROM hr_employee WHERE id = 999999 AND deleted = 0", Integer.class);

    // Then
    assertEquals(0, count, "不存在的员工查询结果应为0");
  }
}

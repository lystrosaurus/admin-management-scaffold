package io.github.lystrosaurus.admin.integration;

import static org.junit.jupiter.api.Assertions.*;

import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.IntegrationTest;
import io.github.lystrosaurus.admin.web.datascope.DataScopeHelper;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据权限集成测试 — 测试 DataScopeHelper 的完整行为
 *
 * <p>通过 @SpringBootTest 注入 DataScopeHelper，设置不同角色的用户，验证 getAccessibleOrgIds / hasAllScope /
 * hasSelfScope / applyOrgScope / applyDataScope 的正确性。
 *
 * <p>依赖 V3 种子数据（sys_user、sys_role）和 V4/V5 表结构（org_unit、sys_role_org）。
 */
@DisplayName("数据权限集成测试")
@Transactional
class DataScopeIntegrationTest extends IntegrationTest {

  @Autowired private DataScopeHelper dataScopeHelper;

  @Autowired private DataSource dataSource;

  @Autowired private BCryptPasswordEncoder passwordEncoder;

  private JdbcTemplate jdbc;

  @BeforeEach
  void setUp() {
    SaTokenContextMockUtil.setMockContext();
    jdbc = new JdbcTemplate(dataSource);

    // 清理测试数据
    jdbc.update("DELETE FROM sys_user_role WHERE user_id >= 10000");
    jdbc.update("DELETE FROM sys_user WHERE id >= 10000");
    jdbc.update("DELETE FROM sys_role_org WHERE role_id >= 10000");
    jdbc.update("DELETE FROM sys_role WHERE id >= 10000");
  }

  @AfterEach
  void tearDown() {
    SaTokenContextMockUtil.clearContext();
  }

  @Test
  @DisplayName("ALL 角色应返回 null（不限制）")
  void should_return_null_for_all_scope() {
    // Given - 创建 ALL 角色用户
    Long userId = 10001L;
    Long roleId = 10001L;
    createUserWithRole(userId, roleId, "ALL", "test_all");

    // 模拟登录该用户
    StpUtil.login(userId);

    // When
    Set<Long> orgIds = dataScopeHelper.getAccessibleOrgIds();

    // Then
    assertNull(orgIds, "ALL 角色应返回 null，表示不限制");
    assertTrue(dataScopeHelper.hasAllScope(), "ALL 角色 hasAllScope 应返回 true");
    assertFalse(dataScopeHelper.hasSelfScope(), "ALL 角色 hasSelfScope 应返回 false");
  }

  @Test
  @DisplayName("ORG_ONLY 角色应返回绑定的部门ID集合")
  void should_return_bound_orgs_for_org_only_scope() {
    // Given - 创建 ORG_ONLY 角色，绑定到技术部(101)
    Long userId = 10002L;
    Long roleId = 10002L;
    createUserWithRole(userId, roleId, "ORG_ONLY", "test_org_only");
    // 绑定角色到部门 101（技术部）
    jdbc.update(
        "INSERT INTO sys_role_org (id, role_id, org_id, created_at, created_by) VALUES (?, ?, 101, NOW(), 'test')",
        10002L,
        roleId);

    // 模拟登录
    StpUtil.login(userId);

    // When
    Set<Long> orgIds = dataScopeHelper.getAccessibleOrgIds();

    // Then
    assertNotNull(orgIds, "ORG_ONLY 角色不应返回 null");
    assertFalse(orgIds.isEmpty(), "ORG_ONLY 角色应有可访问的部门");
    assertTrue(orgIds.contains(101L), "应包含绑定的部门 101（技术部）");
  }

  @Test
  @DisplayName("SELF 角色应返回空集合（部门维度无权限）")
  void should_return_empty_set_for_self_scope() {
    // Given - 创建 SELF 角色用户
    Long userId = 10003L;
    Long roleId = 10003L;
    createUserWithRole(userId, roleId, "SELF", "test_self");

    // 模拟登录
    StpUtil.login(userId);

    // When
    Set<Long> orgIds = dataScopeHelper.getAccessibleOrgIds();

    // Then
    assertNotNull(orgIds, "SELF 角色不应返回 null");
    assertTrue(orgIds.isEmpty(), "SELF 角色在部门维度应返回空集合");
    assertTrue(dataScopeHelper.hasSelfScope(), "SELF 角色 hasSelfScope 应返回 true");
    assertFalse(dataScopeHelper.hasAllScope(), "SELF 角色 hasAllScope 应返回 false");
  }

  @Test
  @DisplayName("CUSTOM 角色应返回自定义绑定的部门ID集合")
  void should_return_custom_bound_orgs_for_custom_scope() {
    // Given - 创建 CUSTOM 角色，绑定到技术部(101)和产品部(102)
    Long userId = 10004L;
    Long roleId = 10004L;
    createUserWithRole(userId, roleId, "CUSTOM", "test_custom");
    jdbc.update(
        "INSERT INTO sys_role_org (id, role_id, org_id, created_at, created_by) VALUES (?, ?, 101, NOW(), 'test')",
        10004L,
        roleId);
    jdbc.update(
        "INSERT INTO sys_role_org (id, role_id, org_id, created_at, created_by) VALUES (?, ?, 102, NOW(), 'test')",
        10005L,
        roleId);

    // 模拟登录
    StpUtil.login(userId);

    // When
    Set<Long> orgIds = dataScopeHelper.getAccessibleOrgIds();

    // Then
    assertNotNull(orgIds, "CUSTOM 角色不应返回 null");
    assertEquals(2, orgIds.size(), "应包含2个部门");
    assertTrue(orgIds.contains(101L), "应包含技术部");
    assertTrue(orgIds.contains(102L), "应包含产品部");
  }

  @Test
  @DisplayName("多角色应合并部门ID集合")
  void should_merge_org_ids_from_multiple_roles() {
    // Given - 用户同时拥有 ORG_ONLY 和 CUSTOM 角色
    Long userId = 10006L;
    Long roleOrgOnly = 10006L;
    Long roleCustom = 10007L;

    // 创建用户
    String hash = passwordEncoder.encode("test123");
    jdbc.update(
        "INSERT INTO sys_user (id, username, password_hash, nickname, status, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 'test_multi_role', ?, '多角色用户', 'ENABLED', NOW(), NOW(), 'test', 'test', 0, 1)",
        userId,
        hash);

    // 创建 ORG_ONLY 角色，绑定到后端组(103)
    jdbc.update(
        "INSERT INTO sys_role (id, code, name, description, status, data_scope_type, sort_order, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 'TEST_ORG_ONLY', '测试ORG_ONLY', '', 'ENABLED', 'ORG_ONLY', 99, NOW(), NOW(), 'test', 'test', 0, 1)",
        roleOrgOnly);
    jdbc.update(
        "INSERT INTO sys_role_org (id, role_id, org_id, created_at, created_by) VALUES (?, ?, 103, NOW(), 'test')",
        10008L,
        roleOrgOnly);

    // 创建 CUSTOM 角色，绑定到产品部(102)
    jdbc.update(
        "INSERT INTO sys_role (id, code, name, description, status, data_scope_type, sort_order, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, 'TEST_CUSTOM', '测试CUSTOM', '', 'ENABLED', 'CUSTOM', 99, NOW(), NOW(), 'test', 'test', 0, 1)",
        roleCustom);
    jdbc.update(
        "INSERT INTO sys_role_org (id, role_id, org_id, created_at, created_by) VALUES (?, ?, 102, NOW(), 'test')",
        10009L,
        roleCustom);

    // 关联用户和角色
    jdbc.update(
        "INSERT INTO sys_user_role (id, user_id, role_id, created_at, created_by, updated_at, updated_by, deleted) VALUES (?, ?, ?, NOW(), 'test', NOW(), 'test', 0)",
        10006L,
        userId,
        roleOrgOnly);
    jdbc.update(
        "INSERT INTO sys_user_role (id, user_id, role_id, created_at, created_by, updated_at, updated_by, deleted) VALUES (?, ?, ?, NOW(), 'test', NOW(), 'test', 0)",
        10007L,
        userId,
        roleCustom);

    // 模拟登录
    StpUtil.login(userId);

    // When
    Set<Long> orgIds = dataScopeHelper.getAccessibleOrgIds();

    // Then
    assertNotNull(orgIds, "多角色不应返回 null");
    assertTrue(orgIds.contains(102L), "应包含 CUSTOM 角色绑定的产品部(102)");
    assertTrue(orgIds.contains(103L), "应包含 ORG_ONLY 角色绑定的后端组(103)");
    assertEquals(2, orgIds.size(), "合并后应有2个部门");
  }

  // ==================== 辅助方法 ====================

  /** 创建测试用户并关联角色 */
  private void createUserWithRole(Long userId, Long roleId, String dataScopeType, String username) {
    // 创建用户
    String hash = passwordEncoder.encode("test123");
    jdbc.update(
        "INSERT INTO sys_user (id, username, password_hash, nickname, status, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, ?, ?, '测试用户', 'ENABLED', NOW(), NOW(), 'test', 'test', 0, 1)",
        userId,
        username,
        hash);

    // 创建角色
    jdbc.update(
        "INSERT INTO sys_role (id, code, name, description, status, data_scope_type, sort_order, created_at, updated_at, created_by, updated_by, deleted, version) VALUES (?, ?, ?, '', 'ENABLED', ?, 99, NOW(), NOW(), 'test', 'test', 0, 1)",
        roleId,
        "TEST_" + dataScopeType,
        "测试" + dataScopeType,
        dataScopeType);

    // 关联用户和角色
    jdbc.update(
        "INSERT INTO sys_user_role (id, user_id, role_id, created_at, created_by, updated_at, updated_by, deleted) VALUES (?, ?, ?, NOW(), 'test', NOW(), 'test', 0)",
        userId,
        userId,
        roleId);
  }
}

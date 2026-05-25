package io.github.lystrosaurus.admin.db;

import static org.junit.jupiter.api.Assertions.*;

import cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate;
import io.github.lystrosaurus.admin.config.TestDatabaseInitializer;
import io.github.lystrosaurus.admin.config.TestRedisConfig;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

/** Flyway 迁移测试 — 验证所有迁移脚本和种子数据 */
@DisplayName("Flyway 迁移测试")
@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(
    exclude = {
      DataRedisAutoConfiguration.class,
      RedissonAutoConfigurationV2.class,
      SaTokenDaoForRedisTemplate.class
    })
@Import({TestDatabaseInitializer.class, TestRedisConfig.class})
class FlywayMigrationTest {

  @Autowired private DataSource dataSource;

  @Autowired private BCryptPasswordEncoder passwordEncoder;

  private JdbcTemplate jdbc() {
    return new JdbcTemplate(dataSource);
  }

  // ==================== V1 表结构验证 ====================

  @Test
  @DisplayName("V1: 应创建 sys_user 表")
  void v1_should_create_sys_user_table() {
    List<Map<String, Object>> tables = jdbc().queryForList("SHOW TABLES LIKE 'sys_user'");
    assertFalse(tables.isEmpty(), "sys_user 表应该存在");
  }

  @Test
  @DisplayName("V1: 应创建 sys_role 表")
  void v1_should_create_sys_role_table() {
    List<Map<String, Object>> tables = jdbc().queryForList("SHOW TABLES LIKE 'sys_role'");
    assertFalse(tables.isEmpty(), "sys_role 表应该存在");
  }

  @Test
  @DisplayName("V1: 应创建 sys_user_role 表")
  void v1_should_create_sys_user_role_table() {
    List<Map<String, Object>> tables = jdbc().queryForList("SHOW TABLES LIKE 'sys_user_role'");
    assertFalse(tables.isEmpty(), "sys_user_role 表应该存在");
  }

  @Test
  @DisplayName("V1: 应创建 sys_permission 表")
  void v1_should_create_sys_permission_table() {
    List<Map<String, Object>> tables = jdbc().queryForList("SHOW TABLES LIKE 'sys_permission'");
    assertFalse(tables.isEmpty(), "sys_permission 表应该存在");
  }

  @Test
  @DisplayName("V1: 应创建 sys_role_permission 表")
  void v1_should_create_sys_role_permission_table() {
    List<Map<String, Object>> tables =
        jdbc().queryForList("SHOW TABLES LIKE 'sys_role_permission'");
    assertFalse(tables.isEmpty(), "sys_role_permission 表应该存在");
  }

  // ==================== V2 表结构验证 ====================

  @Test
  @DisplayName("V2: 应创建 sys_menu 表")
  void v2_should_create_sys_menu_table() {
    List<Map<String, Object>> tables = jdbc().queryForList("SHOW TABLES LIKE 'sys_menu'");
    assertFalse(tables.isEmpty(), "sys_menu 表应该存在");
  }

  @Test
  @DisplayName("V2: 应创建 sys_role_menu 表")
  void v2_should_create_sys_role_menu_table() {
    List<Map<String, Object>> tables = jdbc().queryForList("SHOW TABLES LIKE 'sys_role_menu'");
    assertFalse(tables.isEmpty(), "sys_role_menu 表应该存在");
  }

  @Test
  @DisplayName("V2: sys_permission 应有 type/module/resource/action 列")
  void v2_should_add_permission_extended_columns() {
    List<Map<String, Object>> columns = jdbc().queryForList("DESCRIBE sys_permission");

    boolean hasType = columns.stream().anyMatch(c -> "type".equals(c.get("Field")));
    boolean hasModule = columns.stream().anyMatch(c -> "module".equals(c.get("Field")));
    boolean hasResource = columns.stream().anyMatch(c -> "resource".equals(c.get("Field")));
    boolean hasAction = columns.stream().anyMatch(c -> "action".equals(c.get("Field")));

    assertTrue(hasType, "sys_permission 应有 type 列");
    assertTrue(hasModule, "sys_permission 应有 module 列");
    assertTrue(hasResource, "sys_permission 应有 resource 列");
    assertTrue(hasAction, "sys_permission 应有 action 列");
  }

  // ==================== V3 列名修复验证 ====================

  @Test
  @DisplayName("V3: sys_user 应有 password_hash 列（已重命名）")
  void v3_should_have_password_hash_column() {
    List<Map<String, Object>> columns = jdbc().queryForList("DESCRIBE sys_user");

    boolean hasPasswordHash =
        columns.stream().anyMatch(c -> "password_hash".equals(c.get("Field")));
    boolean hasOldPassword = columns.stream().anyMatch(c -> "password".equals(c.get("Field")));

    assertTrue(hasPasswordHash, "sys_user 应有 password_hash 列");
    assertFalse(hasOldPassword, "sys_user 不应有旧的 password 列");
  }

  @Test
  @DisplayName("V3: sys_role 应有 code/name 列（已重命名）")
  void v3_should_have_renamed_role_columns() {
    List<Map<String, Object>> columns = jdbc().queryForList("DESCRIBE sys_role");

    boolean hasCode = columns.stream().anyMatch(c -> "code".equals(c.get("Field")));
    boolean hasName = columns.stream().anyMatch(c -> "name".equals(c.get("Field")));
    boolean hasOldRoleCode = columns.stream().anyMatch(c -> "role_code".equals(c.get("Field")));
    boolean hasOldRoleName = columns.stream().anyMatch(c -> "role_name".equals(c.get("Field")));

    assertTrue(hasCode, "sys_role 应有 code 列");
    assertTrue(hasName, "sys_role 应有 name 列");
    assertFalse(hasOldRoleCode, "sys_role 不应有旧的 role_code 列");
    assertFalse(hasOldRoleName, "sys_role 不应有旧的 role_name 列");
  }

  @Test
  @DisplayName("V3: sys_permission 应有 code/name 列（已重命名）")
  void v3_should_have_renamed_permission_columns() {
    List<Map<String, Object>> columns = jdbc().queryForList("DESCRIBE sys_permission");

    boolean hasCode = columns.stream().anyMatch(c -> "code".equals(c.get("Field")));
    boolean hasName = columns.stream().anyMatch(c -> "name".equals(c.get("Field")));
    boolean hasOldPermCode =
        columns.stream().anyMatch(c -> "permission_code".equals(c.get("Field")));
    boolean hasOldPermName =
        columns.stream().anyMatch(c -> "permission_name".equals(c.get("Field")));

    assertTrue(hasCode, "sys_permission 应有 code 列");
    assertTrue(hasName, "sys_permission 应有 name 列");
    assertFalse(hasOldPermCode, "sys_permission 不应有旧的 permission_code 列");
    assertFalse(hasOldPermName, "sys_permission 不应有旧的 permission_name 列");
  }

  // ==================== V3 种子数据验证 ====================

  @Test
  @DisplayName("V3: 应插入管理员用户")
  void v3_should_insert_admin_user() {
    Map<String, Object> admin =
        jdbc()
            .queryForMap(
                "SELECT username, nickname, status FROM sys_user WHERE username = 'admin' AND deleted = 0");

    assertEquals("admin", admin.get("username"));
    assertEquals("超级管理员", admin.get("nickname"));
    assertEquals("ENABLED", admin.get("status"));
  }

  @Test
  @DisplayName("V3: 管理员密码应为有效的 BCrypt 哈希")
  void v3_admin_password_should_be_valid_bcrypt_hash() {
    String hash =
        jdbc()
            .queryForObject(
                "SELECT password_hash FROM sys_user WHERE username = 'admin' AND deleted = 0",
                String.class);

    assertNotNull(hash, "密码哈希不应为空");
    assertTrue(
        hash.startsWith("$2a$") || hash.startsWith("$2b$"),
        "密码应为 BCrypt 格式，实际前缀: " + (hash.length() >= 4 ? hash.substring(0, 4) : "null"));
    assertTrue(passwordEncoder.matches("admin123", hash), "密码哈希应匹配 'admin123'");
  }

  @Test
  @DisplayName("V3: 应插入管理员和普通用户角色")
  void v3_should_insert_roles() {
    Long roleCount =
        jdbc()
            .queryForObject(
                "SELECT COUNT(*) FROM sys_role WHERE code IN ('ADMIN', 'USER') AND deleted = 0",
                Long.class);

    assertEquals(2L, roleCount, "应有 ADMIN 和 USER 两个角色");
  }

  @Test
  @DisplayName("V3: 应关联管理员用户和角色")
  void v3_should_assign_admin_role_to_admin_user() {
    Long count =
        jdbc()
            .queryForObject(
                """
                SELECT COUNT(*) FROM sys_user_role ur
                JOIN sys_user u ON ur.user_id = u.id AND u.deleted = 0
                JOIN sys_role r ON ur.role_id = r.id AND r.deleted = 0
                WHERE u.username = 'admin' AND r.code = 'ADMIN'
                """,
                Long.class);

    assertEquals(1L, count, "admin 用户应关联 ADMIN 角色");
  }

  @Test
  @DisplayName("V3: 应插入 8 条基础权限")
  void v3_should_insert_permissions() {
    Long count =
        jdbc().queryForObject("SELECT COUNT(*) FROM sys_permission WHERE deleted = 0", Long.class);

    assertEquals(8L, count, "应有 8 条基础权限");
  }

  @Test
  @DisplayName("V3: 应插入 5 条基础菜单")
  void v3_should_insert_menus() {
    Long count =
        jdbc().queryForObject("SELECT COUNT(*) FROM sys_menu WHERE deleted = 0", Long.class);

    assertEquals(5L, count, "应有 5 条基础菜单");
  }
}

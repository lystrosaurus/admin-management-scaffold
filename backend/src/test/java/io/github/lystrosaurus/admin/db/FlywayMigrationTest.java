package io.github.lystrosaurus.admin.db;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.config.TestDatabaseInitializer;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/** Flyway 迁移测试 */
@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(
    exclude = {
      RedisAutoConfiguration.class,
      RedisRepositoriesAutoConfiguration.class,
      RedissonAutoConfigurationV2.class
    })
@Import(TestDatabaseInitializer.class)
class FlywayMigrationTest {

  @Autowired private DataSource dataSource;

  @Test
  void should_migrate_database_successfully() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    // 验证用户表是否存在
    List<Map<String, Object>> tables = jdbcTemplate.queryForList("SHOW TABLES LIKE 'sys_user'");
    assertFalse(tables.isEmpty(), "sys_user 表应该存在");

    // 验证角色表是否存在
    tables = jdbcTemplate.queryForList("SHOW TABLES LIKE 'sys_role'");
    assertFalse(tables.isEmpty(), "sys_role 表应该存在");

    // 验证用户角色关联表是否存在
    tables = jdbcTemplate.queryForList("SHOW TABLES LIKE 'sys_user_role'");
    assertFalse(tables.isEmpty(), "sys_user_role 表应该存在");

    // 验证权限表是否存在
    tables = jdbcTemplate.queryForList("SHOW TABLES LIKE 'sys_permission'");
    assertFalse(tables.isEmpty(), "sys_permission 表应该存在");

    // 验证角色权限关联表是否存在
    tables = jdbcTemplate.queryForList("SHOW TABLES LIKE 'sys_role_permission'");
    assertFalse(tables.isEmpty(), "sys_role_permission 表应该存在");
  }

  @Test
  void should_have_correct_table_structure() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    // 验证用户表结构
    List<Map<String, Object>> columns = jdbcTemplate.queryForList("DESCRIBE sys_user");
    assertFalse(columns.isEmpty(), "sys_user 表应该有列定义");

    // 验证关键列是否存在
    boolean hasId =
        columns.stream().anyMatch(col -> "id".equalsIgnoreCase((String) col.get("Field")));
    boolean hasUsername =
        columns.stream().anyMatch(col -> "username".equalsIgnoreCase((String) col.get("Field")));
    boolean hasPassword =
        columns.stream().anyMatch(col -> "password".equalsIgnoreCase((String) col.get("Field")));

    assertTrue(hasId, "sys_user 表应该有 id 列");
    assertTrue(hasUsername, "sys_user 表应该有 username 列");
    assertTrue(hasPassword, "sys_user 表应该有 password 列");
  }
}

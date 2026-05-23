package io.github.lystrosaurus.admin.config;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * 测试数据库初始化器。
 *
 * <p>在测试前创建 admin_scaffold_test 数据库（如不存在），清理所有表后手动执行 Flyway 迁移。
 */
@Slf4j
@TestConfiguration
public class TestDatabaseInitializer {

  private static final String URL =
      "jdbc:mysql://localhost:3306/admin_scaffold_test"
          + "?useUnicode=true&characterEncoding=utf8&useSSL=false"
          + "&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";

  private static final String ADMIN_URL =
      "jdbc:mysql://localhost:3306"
          + "?useUnicode=true&characterEncoding=utf8&useSSL=false"
          + "&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";

  /** 创建并清理测试数据库，然后手动执行 Flyway 迁移 — 确保每次从零开始 */
  @Bean
  public DataSource testDataSource() {
    // 1. 确保数据库存在
    JdbcTemplate adminJdbc =
        new JdbcTemplate(new DriverManagerDataSource(ADMIN_URL, "root", "root"));
    try {
      adminJdbc.execute(
          "CREATE DATABASE IF NOT EXISTS admin_scaffold_test"
              + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
      log.info("测试数据库 admin_scaffold_test 创建成功或已存在");
    } catch (Exception e) {
      log.error("创建测试数据库失败", e);
    }

    // 2. 清理所有表（外键检查已关闭）
    JdbcTemplate cleanupJdbc = new JdbcTemplate(new DriverManagerDataSource(URL, "root", "root"));
    try {
      cleanupJdbc.execute("SET FOREIGN_KEY_CHECKS = 0");
      try {
        cleanupJdbc.execute("DROP TABLE IF EXISTS flyway_schema_history");
        for (String table :
            new String[] {
              "auth_external_account",
              "auth_provider",
              "identity_link_candidate",
              "ext_principal_identifier",
              "ext_principal",
              "ext_source",
              "employee_org",
              "sys_role_org",
              "hr_employee",
              "org_unit",
              "sys_role_menu",
              "sys_role_permission",
              "sys_user_role",
              "sys_menu",
              "sys_permission",
              "sys_role",
              "sys_user"
            }) {
          cleanupJdbc.execute("DROP TABLE IF EXISTS " + table);
        }
      } finally {
        cleanupJdbc.execute("SET FOREIGN_KEY_CHECKS = 1");
      }
      log.info("测试数据库已清理，将手动执行 Flyway 迁移");
    } catch (Exception e) {
      log.error("清理测试数据库失败", e);
    }

    // 3. 手动执行 Flyway 迁移（绕过 Spring 自动配置，避免生命周期冲突）
    try {
      Flyway flyway =
          Flyway.configure()
              .dataSource(URL, "root", "root")
              .locations("classpath:db/migration")
              .baselineOnMigrate(true)
              .load();
      flyway.migrate();
      log.info("Flyway 迁移完成");
    } catch (Exception e) {
      log.error("Flyway 迁移失败", e);
    }

    // 4. 返回测试数据源
    return new DriverManagerDataSource(URL, "root", "root");
  }
}

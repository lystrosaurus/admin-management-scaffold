package io.github.lystrosaurus.admin.config;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/** 测试数据库初始化器 在测试前创建 admin_scaffold_test 数据库（如不存在） */
@Slf4j
@TestConfiguration
public class TestDatabaseInitializer {

  /** 创建并清理测试数据库 — 确保 Flyway 每次从零开始执行 */
  @Bean
  public DataSource testDataSource() {
    // 先连接到 MySQL 默认数据库创建测试数据库
    DriverManagerDataSource adminDataSource = new DriverManagerDataSource();
    adminDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    adminDataSource.setUrl(
        "jdbc:mysql://localhost:3306?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true");
    adminDataSource.setUsername("root");
    adminDataSource.setPassword("root");

    JdbcTemplate jdbcTemplate = new JdbcTemplate(adminDataSource);
    try {
      jdbcTemplate.execute(
          "CREATE DATABASE IF NOT EXISTS admin_scaffold_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
      log.info("测试数据库 admin_scaffold_test 创建成功或已存在");
    } catch (Exception e) {
      log.error("创建测试数据库失败", e);
    }

    // 直接连接到测试数据库进行清理（USE 在 JDBC 中不跨 Statement 生效）
    DriverManagerDataSource cleanupDataSource = new DriverManagerDataSource();
    cleanupDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    cleanupDataSource.setUrl(
        "jdbc:mysql://localhost:3306/admin_scaffold_test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true");
    cleanupDataSource.setUsername("root");
    cleanupDataSource.setPassword("root");

    JdbcTemplate cleanupJdbc = new JdbcTemplate(cleanupDataSource);
    try {
      cleanupJdbc.execute("SET FOREIGN_KEY_CHECKS = 0");
      try {
        cleanupJdbc.execute("DROP TABLE IF EXISTS flyway_schema_history");
        for (String table :
            new String[] {
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
      log.info("测试数据库已清理，Flyway 将重新执行所有迁移");
    } catch (Exception e) {
      log.error("清理测试数据库失败", e);
    }

    // 返回实际的测试数据源
    DriverManagerDataSource testDataSource = new DriverManagerDataSource();
    testDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    testDataSource.setUrl(
        "jdbc:mysql://localhost:3306/admin_scaffold_test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true");
    testDataSource.setUsername("root");
    testDataSource.setPassword("root");

    return testDataSource;
  }
}

package io.github.lystrosaurus.admin;

import org.junit.jupiter.api.Test;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ActiveProfiles;

/** 集成测试基类 */
@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(
    exclude = {
      RedisAutoConfiguration.class,
      RedisRepositoriesAutoConfiguration.class,
      RedissonAutoConfigurationV2.class
    })
public abstract class IntegrationTest {

  /** 在 Spring 上下文加载前清理数据库，确保 Flyway 从零执行 */
  static {
    try {
      // 先确保数据库存在
      DriverManagerDataSource adminDs = new DriverManagerDataSource();
      adminDs.setDriverClassName("com.mysql.cj.jdbc.Driver");
      adminDs.setUrl(
          "jdbc:mysql://localhost:3306?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true");
      adminDs.setUsername("root");
      adminDs.setPassword("root");
      new JdbcTemplate(adminDs)
          .execute(
              "CREATE DATABASE IF NOT EXISTS admin_scaffold_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");

      // 直接连接到测试数据库进行清理
      DriverManagerDataSource ds = new DriverManagerDataSource();
      ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
      ds.setUrl(
          "jdbc:mysql://localhost:3306/admin_scaffold_test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true");
      ds.setUsername("root");
      ds.setPassword("root");
      JdbcTemplate jdbc = new JdbcTemplate(ds);
      jdbc.execute("SET FOREIGN_KEY_CHECKS = 0");
      try {
        jdbc.execute("DROP TABLE IF EXISTS flyway_schema_history");
        for (String t :
            new String[] {
              "sys_role_menu",
              "sys_role_permission",
              "sys_user_role",
              "sys_menu",
              "sys_permission",
              "sys_role",
              "sys_user"
            }) {
          jdbc.execute("DROP TABLE IF EXISTS " + t);
        }
      } finally {
        jdbc.execute("SET FOREIGN_KEY_CHECKS = 1");
      }
    } catch (Exception e) {
      // 忽略清理失败（数据库可能不存在）
    }
  }

  @Test
  void contextLoads() {
    // 测试 Spring 上下文加载
  }
}

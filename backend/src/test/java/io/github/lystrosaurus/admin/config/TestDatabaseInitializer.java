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

  @Bean
  public DataSource testDataSource() {
    // 先连接到 MySQL 默认数据库创建测试数据库
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setUrl(
        "jdbc:mysql://localhost:3306?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true");
    dataSource.setUsername("root");
    dataSource.setPassword("root");

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    try {
      jdbcTemplate.execute(
          "CREATE DATABASE IF NOT EXISTS admin_scaffold_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
      log.info("测试数据库 admin_scaffold_test 创建成功或已存在");
    } catch (Exception e) {
      log.error("创建测试数据库失败", e);
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

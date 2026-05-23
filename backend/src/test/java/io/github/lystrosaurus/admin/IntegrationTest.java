package io.github.lystrosaurus.admin;

import org.junit.jupiter.api.Test;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 集成测试基类。
 *
 * <p>数据库初始化由 {@code TestDatabaseInitializer} 负责。TestDatabaseInitializer 会在 Flyway 迁移前清理所有表（包括 V4
 * 新增的组织模块表），确保 Flyway 从零执行。
 */
@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(
    exclude = {
      RedisAutoConfiguration.class,
      RedisRepositoriesAutoConfiguration.class,
      RedissonAutoConfigurationV2.class
    })
public abstract class IntegrationTest {

  @Test
  void contextLoads() {
    // 测试 Spring 上下文加载
  }
}

package io.github.lystrosaurus.admin;

import org.junit.jupiter.api.Test;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** 基础测试类 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EnableAutoConfiguration(
    exclude = {
      RedisAutoConfiguration.class,
      RedisRepositoriesAutoConfiguration.class,
      RedissonAutoConfigurationV2.class
    })
public class BaseTest {

  @Test
  void contextLoads() {
    // 测试 Spring 上下文加载
  }
}

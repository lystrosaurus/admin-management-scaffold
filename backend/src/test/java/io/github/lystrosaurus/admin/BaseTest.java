package io.github.lystrosaurus.admin;

import cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate;
import io.github.lystrosaurus.admin.config.TestRedisConfig;
import org.junit.jupiter.api.Test;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/** 基础测试类 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EnableAutoConfiguration(
    exclude = {
      DataRedisAutoConfiguration.class,
      RedissonAutoConfigurationV2.class,
      SaTokenDaoForRedisTemplate.class
    })
@Import(TestRedisConfig.class)
public class BaseTest {

  @Test
  void contextLoads() {
    // 测试 Spring 上下文加载
  }
}

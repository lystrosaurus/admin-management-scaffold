package io.github.lystrosaurus.admin.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.lystrosaurus.admin.BaseTest;
import io.github.lystrosaurus.admin.shared.kernel.exception.ErrorCode;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/** 登录限流集成测试：验证 Redis 计数与 429 响应。 */
@DisplayName("限流集成测试")
@TestPropertySource(properties = "app.rate-limit.enabled=true")
class RateLimitIntegrationTest extends BaseTest {

  private static final int LOGIN_LIMIT = 5;

  @Autowired private MockMvc mockMvc;

  @Autowired private StringRedisTemplate stringRedisTemplate;

  private String redisKey;

  @AfterEach
  void cleanUp() {
    if (redisKey != null) {
      stringRedisTemplate.delete(redisKey);
    }
  }

  @Test
  @DisplayName("同一用户名第 6 次登录请求应返回 429")
  void should_return_429_on_sixth_login_attempt() throws Exception {
    String username = "ratelimit_" + UUID.randomUUID().toString().replace("-", "");
    redisKey = "ratelimit:auth:login:" + username + ":127.0.0.1";
    String body = "{\"username\":\"" + username + "\",\"password\":\"password123\"}";

    for (int i = 1; i <= LOGIN_LIMIT; i++) {
      mockMvc
          .perform(post("/public/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()));
    }

    mockMvc
        .perform(post("/public/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.SYSTEM_429.getCode()))
        .andExpect(jsonPath("$.message").value("请求过于频繁，请稍后再试"));
  }
}

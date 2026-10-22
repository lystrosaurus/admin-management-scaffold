package io.github.lystrosaurus.admin.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.github.lystrosaurus.admin.BaseTest;
import io.github.lystrosaurus.admin.shared.kernel.exception.ErrorCode;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

/** 写接口幂等集成测试：验证相同请求体第二次返回 409。 */
@DisplayName("幂等集成测试")
@TestPropertySource(properties = "app.idempotent.enabled=true")
@Transactional
class IdempotentIntegrationTest extends BaseTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private StringRedisTemplate stringRedisTemplate;

  @AfterEach
  void cleanUp() {
    Set<String> keys = stringRedisTemplate.keys("idempotent:user:create:*");
    if (keys != null && !keys.isEmpty()) {
      stringRedisTemplate.delete(keys);
    }
    stringRedisTemplate.delete("satoken:login:token-version:1");
  }

  @Test
  @DisplayName("相同 body 连续两次创建用户，第二次应返回 409")
  void should_return_409_on_duplicate_create_user() throws Exception {
    String token = loginAsAdmin();
    String username = "idem_" + UUID.randomUUID().toString().replace("-", "");
    String body =
        "{\"username\":\""
            + username
            + "\",\"password\":\"password123\",\"nickname\":\"幂等测试\"}";

    mockMvc
        .perform(
            post("/app/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));

    mockMvc
        .perform(
            post("/app/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.SYSTEM_409.getCode()))
        .andExpect(jsonPath("$.message").value("重复请求，请稍后再试"));
  }

  private String loginAsAdmin() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post("/public/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andReturn();
    return JsonPath.read(result.getResponse().getContentAsString(), "$.data.accessToken");
  }
}

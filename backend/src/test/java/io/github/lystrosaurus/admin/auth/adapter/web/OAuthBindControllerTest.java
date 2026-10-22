package io.github.lystrosaurus.admin.auth.adapter.web;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.lystrosaurus.admin.auth.adapter.request.OAuthBindRequest;
import io.github.lystrosaurus.admin.auth.adapter.response.ExternalAccountResponse;
import io.github.lystrosaurus.admin.auth.application.OAuthApplicationService;
import io.github.lystrosaurus.admin.shared.kernel.exception.BusinessException;
import io.github.lystrosaurus.admin.shared.kernel.exception.ErrorCode;
import io.github.lystrosaurus.admin.test.SaTokenTest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

/** OAuthBindController MVC 测试 */
@DisplayName("OAuthBindController 测试")
class OAuthBindControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private OAuthApplicationService oauthApplicationService;

  private ExternalAccountResponse externalAccountResponse;
  private OAuthBindRequest bindRequest;

  @BeforeEach
  void setUp() {
    externalAccountResponse =
        new ExternalAccountResponse(
            1L,
            10L,
            "LARK",
            "lark_user_123",
            TEST_USER_ID,
            null,
            "飞书用户",
            "https://example.com/avatar.jpg",
            "BOUND",
            LocalDateTime.of(2025, 1, 1, 10, 0),
            LocalDateTime.of(2025, 1, 1, 9, 0),
            LocalDateTime.of(2025, 1, 1, 10, 0));

    bindRequest = new OAuthBindRequest("auth_code_123", "state_abc");
  }

  @Test
  @DisplayName("POST /app/auth/oauth/{provider}/bind — 正常绑定")
  void should_bind_oauth_account_successfully() throws Exception {
    when(oauthApplicationService.bindAccount(eq("lark"), eq("auth_code_123"), eq("state_abc")))
        .thenReturn(externalAccountResponse);

    mockMvc
        .perform(
            post("/app/auth/oauth/lark/bind")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bindRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.providerCode").value("LARK"))
        .andExpect(jsonPath("$.data.providerUserId").value("lark_user_123"))
        .andExpect(jsonPath("$.data.bindStatus").value("BOUND"));
  }

  @Test
  @DisplayName("POST /app/auth/oauth/{provider}/bind — 三方账号已被绑定")
  void should_return_error_when_bind_account_already_exists() throws Exception {
    when(oauthApplicationService.bindAccount(eq("lark"), eq("auth_code_123"), eq("state_abc")))
        .thenThrow(new BusinessException(ErrorCode.BIND_ACCOUNT_ALREADY_EXISTS));

    mockMvc
        .perform(
            post("/app/auth/oauth/lark/bind")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bindRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.BIND_ACCOUNT_ALREADY_EXISTS.getCode()));
  }

  @Test
  @DisplayName("DELETE /app/auth/oauth/{provider}/unbind — 正常解绑")
  void should_unbind_oauth_account_successfully() throws Exception {
    doNothing().when(oauthApplicationService).unbindAccount("lark", 1L);

    mockMvc
        .perform(delete("/app/auth/oauth/lark/unbind").param("accountId", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("DELETE /app/auth/oauth/{provider}/unbind — 最后登录方式")
  void should_return_error_when_unbind_last_login_method() throws Exception {
    doThrow(new BusinessException(ErrorCode.UNBIND_LAST_LOGIN_METHOD))
        .when(oauthApplicationService)
        .unbindAccount("lark", 1L);

    mockMvc
        .perform(delete("/app/auth/oauth/lark/unbind").param("accountId", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.UNBIND_LAST_LOGIN_METHOD.getCode()));
  }
}

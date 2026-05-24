package io.github.lystrosaurus.admin.auth.oauth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthBindDTO;
import io.github.lystrosaurus.admin.auth.oauth.service.OAuthService;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.test.SaTokenTest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** OAuthBindController MVC 测试 */
@DisplayName("OAuthBindController 测试")
class OAuthBindControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private OAuthService oauthService;

  private ExternalAccountVO externalAccountVO;
  private OAuthBindDTO bindDTO;

  @BeforeEach
  void setUp() {
    externalAccountVO =
        new ExternalAccountVO(
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

    bindDTO = new OAuthBindDTO("auth_code_123", "state_abc");
  }

  @Test
  @DisplayName("POST /app/auth/oauth/{provider}/bind — 正常绑定")
  void should_bind_oauth_account_successfully() throws Exception {
    when(oauthService.bindAccount(eq(TEST_USER_ID), eq("lark"), any(OAuthBindDTO.class)))
        .thenReturn(externalAccountVO);

    mockMvc
        .perform(
            post("/app/auth/oauth/lark/bind")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bindDTO)))
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
    when(oauthService.bindAccount(eq(TEST_USER_ID), eq("lark"), any(OAuthBindDTO.class)))
        .thenThrow(new BusinessException(ErrorCode.BIND_ACCOUNT_ALREADY_EXISTS));

    mockMvc
        .perform(
            post("/app/auth/oauth/lark/bind")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bindDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.BIND_ACCOUNT_ALREADY_EXISTS.getCode()));
  }

  @Test
  @DisplayName("DELETE /app/auth/oauth/{provider}/unbind — 正常解绑")
  void should_unbind_oauth_account_successfully() throws Exception {
    doNothing().when(oauthService).unbindAccount(TEST_USER_ID, "lark", 1L);

    mockMvc
        .perform(delete("/app/auth/oauth/lark/unbind").param("accountId", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("DELETE /app/auth/oauth/{provider}/unbind — 最后登录方式")
  void should_return_error_when_unbind_last_login_method() throws Exception {
    doThrow(new BusinessException(ErrorCode.UNBIND_LAST_LOGIN_METHOD))
        .when(oauthService)
        .unbindAccount(TEST_USER_ID, "lark", 1L);

    mockMvc
        .perform(delete("/app/auth/oauth/lark/unbind").param("accountId", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.UNBIND_LAST_LOGIN_METHOD.getCode()));
  }
}

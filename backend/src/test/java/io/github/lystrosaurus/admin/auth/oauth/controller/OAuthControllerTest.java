package io.github.lystrosaurus.admin.auth.oauth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.lystrosaurus.admin.BaseTest;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthCallbackDTO;
import io.github.lystrosaurus.admin.auth.oauth.service.OAuthService;
import io.github.lystrosaurus.admin.auth.oauth.vo.OAuthAuthorizeVO;
import io.github.lystrosaurus.admin.auth.oauth.vo.OAuthLoginVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** OAuthController MVC 测试 */
@DisplayName("OAuthController 测试")
class OAuthControllerTest extends BaseTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private OAuthService oauthService;

  @Test
  @DisplayName("GET /public/oauth/{provider}/authorize — 正常获取授权 URL")
  void should_return_authorize_url() throws Exception {
    OAuthAuthorizeVO vo = new OAuthAuthorizeVO("https://open.feishu.cn/authorize?state=abc", "abc");
    when(oauthService.authorize("LARK")).thenReturn(vo);

    mockMvc
        .perform(get("/public/oauth/LARK/authorize"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(
            jsonPath("$.data.authorizeUrl").value("https://open.feishu.cn/authorize?state=abc"))
        .andExpect(jsonPath("$.data.state").value("abc"));
  }

  @Test
  @DisplayName("GET /public/oauth/{provider}/authorize — 提供方不存在")
  void should_return_error_when_provider_not_found() throws Exception {
    when(oauthService.authorize("UNKNOWN"))
        .thenThrow(new BusinessException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND));

    mockMvc
        .perform(get("/public/oauth/UNKNOWN/authorize"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.OAUTH_PROVIDER_NOT_FOUND.getCode()));
  }

  @Test
  @DisplayName("GET /public/oauth/{provider}/callback — 已绑定用户登录")
  void should_handle_callback_for_bound_user() throws Exception {
    OAuthLoginVO vo = new OAuthLoginVO(null, false, null);
    when(oauthService.handleCallback(eq("LARK"), any(OAuthCallbackDTO.class))).thenReturn(vo);

    mockMvc
        .perform(
            get("/public/oauth/LARK/callback")
                .param("code", "auth-code")
                .param("state", "state-abc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.needBind").value(false));
  }

  @Test
  @DisplayName("GET /public/oauth/{provider}/callback — 未绑定用户返回 needBind")
  void should_return_need_bind_for_unbound_user() throws Exception {
    OAuthLoginVO vo = new OAuthLoginVO(null, true, "{\"provider\":\"LARK\"}");
    when(oauthService.handleCallback(eq("LARK"), any(OAuthCallbackDTO.class))).thenReturn(vo);

    mockMvc
        .perform(
            get("/public/oauth/LARK/callback")
                .param("code", "auth-code")
                .param("state", "state-abc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.needBind").value(true))
        .andExpect(jsonPath("$.data.externalUserIdentity").exists());
  }

  @Test
  @DisplayName("GET /public/oauth/{provider}/callback — state 无效")
  void should_return_error_when_state_invalid() throws Exception {
    when(oauthService.handleCallback(eq("LARK"), any(OAuthCallbackDTO.class)))
        .thenThrow(new BusinessException(ErrorCode.OAUTH_STATE_INVALID));

    mockMvc
        .perform(
            get("/public/oauth/LARK/callback")
                .param("code", "auth-code")
                .param("state", "invalid-state"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.OAUTH_STATE_INVALID.getCode()));
  }
}

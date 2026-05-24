package io.github.lystrosaurus.admin.auth.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.lystrosaurus.admin.auth.external.service.ExternalAccountService;
import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import io.github.lystrosaurus.admin.auth.log.service.LoginLogService;
import io.github.lystrosaurus.admin.auth.log.vo.LoginLogVO;
import io.github.lystrosaurus.admin.auth.service.AuthService;
import io.github.lystrosaurus.admin.test.SaTokenTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/** SecurityProfileController MVC 测试 */
@DisplayName("SecurityProfileController 测试")
class SecurityProfileControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AuthService authService;

  @MockBean private ExternalAccountService externalAccountService;

  @MockBean private LoginLogService loginLogService;

  @Test
  @DisplayName("GET /app/profile/security — 有密码 + 有绑定 + 有登录记录")
  void should_return_security_profile_with_password_and_binds_and_logs() throws Exception {
    // Given — 用户有密码
    when(authService.hasPassword(TEST_USER_ID)).thenReturn(true);

    // 有1个绑定
    ExternalAccountVO accountVO =
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
    when(externalAccountService.listByUserId(TEST_USER_ID)).thenReturn(List.of(accountVO));

    // 有登录记录
    LoginLogVO loginLogVO =
        new LoginLogVO(
            1L,
            "OAUTH_LARK",
            "LARK",
            "192.168.1.1",
            "SUCCESS",
            LocalDateTime.of(2025, 1, 1, 10, 0));
    when(loginLogService.getRecentLogins(TEST_USER_ID, 10)).thenReturn(List.of(loginLogVO));

    // When & Then
    mockMvc
        .perform(get("/app/profile/security"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.hasPassword").value(true))
        .andExpect(jsonPath("$.data.boundAccounts").isArray())
        .andExpect(jsonPath("$.data.boundAccounts.length()").value(1))
        .andExpect(jsonPath("$.data.boundAccounts[0].providerCode").value("LARK"))
        .andExpect(jsonPath("$.data.recentLogins").isArray())
        .andExpect(jsonPath("$.data.recentLogins.length()").value(1))
        .andExpect(jsonPath("$.data.recentLogins[0].loginType").value("OAUTH_LARK"))
        .andExpect(jsonPath("$.data.recentLogins[0].ipAddress").value("192.168.1.1"));
  }

  @Test
  @DisplayName("GET /app/profile/security — 无密码无绑定")
  void should_return_security_profile_without_password_and_binds() throws Exception {
    // Given — 用户无密码
    when(authService.hasPassword(TEST_USER_ID)).thenReturn(false);

    // 无绑定
    when(externalAccountService.listByUserId(TEST_USER_ID)).thenReturn(List.of());

    // 无登录记录
    when(loginLogService.getRecentLogins(TEST_USER_ID, 10)).thenReturn(List.of());

    // When & Then
    mockMvc
        .perform(get("/app/profile/security"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.hasPassword").value(false))
        .andExpect(jsonPath("$.data.boundAccounts").isArray())
        .andExpect(jsonPath("$.data.boundAccounts.length()").value(0))
        .andExpect(jsonPath("$.data.recentLogins").isArray())
        .andExpect(jsonPath("$.data.recentLogins.length()").value(0));
  }
}

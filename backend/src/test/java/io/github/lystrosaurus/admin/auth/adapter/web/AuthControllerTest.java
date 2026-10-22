package io.github.lystrosaurus.admin.auth.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.lystrosaurus.admin.BaseTest;
import io.github.lystrosaurus.admin.auth.adapter.request.LoginRequest;
import io.github.lystrosaurus.admin.auth.adapter.response.LoginResponse;
import io.github.lystrosaurus.admin.auth.application.AuthApplicationService;
import io.github.lystrosaurus.admin.auth.application.command.LoginCommand;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

/**
 * AuthController 测试
 *
 * <p>测试认证控制器的各个接口
 */
@DisplayName("AuthController 测试")
class AuthControllerTest extends BaseTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AuthApplicationService authApplicationService;

  private LoginRequest loginRequest;
  private LoginResponse loginResponse;
  private UserResponse userVO;

  @BeforeEach
  void setUp() {
    loginRequest = new LoginRequest("admin", "password123");
    userVO =
        new UserResponse(
            1L,
            "admin",
            "管理员",
            "13800138000",
            "admin@example.com",
            "ENABLED",
            LocalDateTime.now(),
            LocalDateTime.now());
    loginResponse = new LoginResponse("jwt-token-123", userVO);
  }

  @Test
  @DisplayName("应该成功登录")
  void should_login_successfully() throws Exception {
    when(authApplicationService.login(any(LoginCommand.class))).thenReturn(loginResponse);

    mockMvc
        .perform(
            post("/public/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"))
        .andExpect(jsonPath("$.data.accessToken").value("jwt-token-123"))
        .andExpect(jsonPath("$.data.user.username").value("admin"));
  }

  @Test
  @DisplayName("应该返回400当用户名为空")
  void should_return_400_when_username_is_blank() throws Exception {
    LoginRequest invalidRequest = new LoginRequest("", "password123");

    mockMvc
        .perform(
            post("/public/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该返回400当密码为空")
  void should_return_400_when_password_is_blank() throws Exception {
    LoginRequest invalidRequest = new LoginRequest("admin", "");

    mockMvc
        .perform(
            post("/public/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该返回401当用户不存在")
  void should_return_401_when_user_not_found() throws Exception {
    when(authApplicationService.login(any(LoginCommand.class)))
        .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

    mockMvc
        .perform(
            post("/public/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()));
  }

  @Test
  @DisplayName("应该返回401当密码错误")
  void should_return_401_when_password_mismatch() throws Exception {
    when(authApplicationService.login(any(LoginCommand.class)))
        .thenThrow(new BusinessException(ErrorCode.PASSWORD_MISMATCH));

    mockMvc
        .perform(
            post("/public/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PASSWORD_MISMATCH.getCode()));
  }

  @Test
  @DisplayName("应该返回401当用户被禁用")
  void should_return_401_when_user_disabled() throws Exception {
    when(authApplicationService.login(any(LoginCommand.class)))
        .thenThrow(new BusinessException(ErrorCode.USER_DISABLED));

    mockMvc
        .perform(
            post("/public/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.USER_DISABLED.getCode()));
  }

  @Test
  @DisplayName("应该成功登出")
  void should_logout_successfully() throws Exception {
    mockMvc
        .perform(post("/public/auth/logout"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"));
  }

  @Test
  @DisplayName("应该成功刷新Token")
  void should_refresh_token_successfully() throws Exception {
    when(authApplicationService.refreshToken()).thenReturn(loginResponse);

    mockMvc
        .perform(get("/public/auth/refresh"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.accessToken").value("jwt-token-123"));
  }

  @Test
  @DisplayName("应该返回401当未登录时刷新Token")
  void should_return_401_when_refresh_token_without_login() throws Exception {
    when(authApplicationService.refreshToken())
        .thenThrow(new BusinessException(ErrorCode.AUTH_401));

    mockMvc
        .perform(get("/public/auth/refresh"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.AUTH_401.getCode()));
  }
}

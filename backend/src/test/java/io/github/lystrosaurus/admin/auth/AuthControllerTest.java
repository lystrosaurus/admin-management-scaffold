package io.github.lystrosaurus.admin.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lystrosaurus.admin.BaseTest;
import io.github.lystrosaurus.admin.auth.service.AuthService;
import io.github.lystrosaurus.admin.auth.vo.LoginVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.dto.LoginDTO;
import io.github.lystrosaurus.admin.system.user.vo.UserVO;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * AuthController 测试
 *
 * <p>测试认证控制器的各个接口
 */
@DisplayName("AuthController 测试")
class AuthControllerTest extends BaseTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AuthService authService;

  private LoginDTO loginDTO;
  private LoginVO loginVO;
  private UserVO userVO;

  @BeforeEach
  void setUp() {
    loginDTO = new LoginDTO("admin", "password123");
    userVO =
        new UserVO(
            1L,
            "admin",
            "管理员",
            "13800138000",
            "admin@example.com",
            "ENABLED",
            LocalDateTime.now(),
            LocalDateTime.now());
    loginVO = new LoginVO("jwt-token-123", userVO);
  }

  @Test
  @DisplayName("应该成功登录")
  void should_login_successfully() throws Exception {
    when(authService.login(any(LoginDTO.class))).thenReturn(loginVO);

    mockMvc
        .perform(
            post("/public/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"))
        .andExpect(jsonPath("$.data.accessToken").value("jwt-token-123"))
        .andExpect(jsonPath("$.data.user.username").value("admin"));
  }

  @Test
  @DisplayName("应该返回400当用户名为空")
  void should_return_400_when_username_is_blank() throws Exception {
    LoginDTO invalidDTO = new LoginDTO("", "password123");

    mockMvc
        .perform(
            post("/public/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该返回400当密码为空")
  void should_return_400_when_password_is_blank() throws Exception {
    LoginDTO invalidDTO = new LoginDTO("admin", "");

    mockMvc
        .perform(
            post("/public/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该返回401当用户不存在")
  void should_return_401_when_user_not_found() throws Exception {
    when(authService.login(any(LoginDTO.class)))
        .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

    mockMvc
        .perform(
            post("/public/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()));
  }

  @Test
  @DisplayName("应该返回401当密码错误")
  void should_return_401_when_password_mismatch() throws Exception {
    when(authService.login(any(LoginDTO.class)))
        .thenThrow(new BusinessException(ErrorCode.PASSWORD_MISMATCH));

    mockMvc
        .perform(
            post("/public/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PASSWORD_MISMATCH.getCode()));
  }

  @Test
  @DisplayName("应该返回401当用户被禁用")
  void should_return_401_when_user_disabled() throws Exception {
    when(authService.login(any(LoginDTO.class)))
        .thenThrow(new BusinessException(ErrorCode.USER_DISABLED));

    mockMvc
        .perform(
            post("/public/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
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
    when(authService.refreshToken()).thenReturn(loginVO);

    mockMvc
        .perform(get("/public/auth/refresh"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.accessToken").value("jwt-token-123"));
  }

  @Test
  @DisplayName("应该返回401当未登录时刷新Token")
  void should_return_401_when_refresh_token_without_login() throws Exception {
    when(authService.refreshToken()).thenThrow(new BusinessException(ErrorCode.AUTH_401));

    mockMvc
        .perform(get("/public/auth/refresh"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.AUTH_401.getCode()));
  }
}

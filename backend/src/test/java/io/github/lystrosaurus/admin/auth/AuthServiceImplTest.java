package io.github.lystrosaurus.admin.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.service.impl.AuthServiceImpl;
import io.github.lystrosaurus.admin.auth.vo.LoginVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import io.github.lystrosaurus.admin.system.user.dto.LoginDTO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * AuthService 实现单元测试
 *
 * <p>纯 Mockito 测试，不依赖 Spring 上下文
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 实现测试")
class AuthServiceImplTest {

  @Mock private UserDAO userDAO;

  @Mock private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks private AuthServiceImpl authService;

  private SysUser activeUser;
  private SysUser disabledUser;
  private SysUser lockedUser;

  @BeforeEach
  void setUp() {
    // 创建活跃用户
    activeUser = new SysUser();
    activeUser.setId(1L);
    activeUser.setUsername("admin");
    activeUser.setPasswordHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH");
    activeUser.setNickname("管理员");
    activeUser.setPhone("13800138000");
    activeUser.setEmail("admin@example.com");
    activeUser.setStatus("ENABLED");
    activeUser.setTokenVersion(1);
    activeUser.setLastLoginAt(LocalDateTime.now().minusDays(1));
    activeUser.setLastLoginIp("127.0.0.1");

    // 创建禁用用户
    disabledUser = new SysUser();
    disabledUser.setId(2L);
    disabledUser.setUsername("disabled");
    disabledUser.setPasswordHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH");
    disabledUser.setNickname("禁用用户");
    disabledUser.setStatus("DISABLED");

    // 创建锁定用户
    lockedUser = new SysUser();
    lockedUser.setId(3L);
    lockedUser.setUsername("locked");
    lockedUser.setPasswordHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH");
    lockedUser.setNickname("锁定用户");
    lockedUser.setStatus("LOCKED");
  }

  @Test
  @DisplayName("应该抛出异常当用户不存在")
  void should_throw_exception_when_user_not_found() {
    LoginDTO loginDTO = new LoginDTO("nonexistent", "password123");
    when(userDAO.findByUsername("nonexistent")).thenReturn(null);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.login(loginDTO));
    assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该抛出异常当密码错误")
  void should_throw_exception_when_password_mismatch() {
    LoginDTO loginDTO = new LoginDTO("admin", "wrongpassword");
    when(userDAO.findByUsername("admin")).thenReturn(activeUser);
    when(passwordEncoder.matches("wrongpassword", activeUser.getPasswordHash())).thenReturn(false);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.login(loginDTO));
    assertEquals(ErrorCode.USER_INVALID_PASSWORD.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该抛出异常当用户被禁用")
  void should_throw_exception_when_user_disabled() {
    LoginDTO loginDTO = new LoginDTO("disabled", "password123");
    when(userDAO.findByUsername("disabled")).thenReturn(disabledUser);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.login(loginDTO));
    assertEquals(ErrorCode.USER_ACCOUNT_DISABLED.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该抛出异常当用户被锁定")
  void should_throw_exception_when_user_locked() {
    LoginDTO loginDTO = new LoginDTO("locked", "password123");
    when(userDAO.findByUsername("locked")).thenReturn(lockedUser);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.login(loginDTO));
    assertEquals(ErrorCode.USER_ACCOUNT_DISABLED.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功登录当用户名和密码正确")
  void should_login_successfully_when_username_and_password_correct() {
    LoginDTO loginDTO = new LoginDTO("admin", "password123");
    when(userDAO.findByUsername("admin")).thenReturn(activeUser);
    when(passwordEncoder.matches("password123", activeUser.getPasswordHash())).thenReturn(true);

    // 使用 MockedStatic 模拟 StpUtil 静态方法，因为 SaTokenContext 在纯 Mockito 测试中未初始化
    try (var stpUtilMock = mockStatic(cn.dev33.satoken.stp.StpUtil.class)) {
      stpUtilMock
          .when(() -> cn.dev33.satoken.stp.StpUtil.login(org.mockito.ArgumentMatchers.any()))
          .thenAnswer(invocation -> null);
      stpUtilMock.when(cn.dev33.satoken.stp.StpUtil::getTokenValue).thenReturn("mock-jwt-token");

      LoginVO result = authService.login(loginDTO);

      assertNotNull(result);
      assertEquals("mock-jwt-token", result.accessToken());
      assertEquals("admin", result.user().username());
      verify(userDAO).update(any(SysUser.class));
    }
  }
}

package io.github.lystrosaurus.admin.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import io.github.lystrosaurus.admin.system.user.dto.LoginDTO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** AuthService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 测试")
class AuthServiceTest {

  @Mock private UserDAO userDAO;

  @Mock private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks private AuthService authService;

  private SysUser activeUser;
  private SysUser disabledUser;

  @BeforeEach
  void setUp() {
    activeUser = new SysUser();
    activeUser.setId(1L);
    activeUser.setUsername("admin");
    activeUser.setPasswordHash("$2a$10$hashedpassword");
    activeUser.setNickname("管理员");
    activeUser.setStatus("ENABLED");

    disabledUser = new SysUser();
    disabledUser.setId(2L);
    disabledUser.setUsername("disabled");
    disabledUser.setStatus("DISABLED");
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
  @DisplayName("应该抛出异常当用户被禁用")
  void should_throw_exception_when_user_disabled() {
    LoginDTO loginDTO = new LoginDTO("disabled", "password123");
    when(userDAO.findByUsername("disabled")).thenReturn(disabledUser);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.login(loginDTO));
    assertEquals(ErrorCode.USER_ACCOUNT_DISABLED.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该正确判断用户是否设置了密码")
  void should_correctly_check_if_user_has_password() {
    when(userDAO.findById(1L)).thenReturn(activeUser);
    assertTrue(authService.hasPassword(1L));

    activeUser.setPasswordHash(null);
    assertFalse(authService.hasPassword(1L));
  }
}

package io.github.lystrosaurus.admin.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.dto.LoginDTO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.domain.model.UserStatus;
import java.util.Optional;
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

  @Mock private UserRepository userRepository;

  @Mock private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks private AuthService authService;

  private User activeUser;
  private User disabledUser;

  @BeforeEach
  void setUp() {
    activeUser = buildUser(1L, "admin", "$2a$10$hashedpassword", "管理员", UserStatus.ENABLED);
    disabledUser = buildUser(2L, "disabled", null, null, UserStatus.DISABLED);
  }

  @Test
  @DisplayName("应该抛出异常当用户不存在")
  void should_throw_exception_when_user_not_found() {
    LoginDTO loginDTO = new LoginDTO("nonexistent", "password123");
    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.login(loginDTO));
    assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该抛出异常当用户被禁用")
  void should_throw_exception_when_user_disabled() {
    LoginDTO loginDTO = new LoginDTO("disabled", "password123");
    when(userRepository.findByUsername("disabled")).thenReturn(Optional.of(disabledUser));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.login(loginDTO));
    assertEquals(ErrorCode.USER_ACCOUNT_DISABLED.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该正确判断用户是否设置了密码")
  void should_correctly_check_if_user_has_password() {
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(activeUser));
    assertTrue(authService.hasPassword(1L));

    User noPasswordUser = buildUser(1L, "admin", null, "管理员", UserStatus.ENABLED);
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(noPasswordUser));
    assertFalse(authService.hasPassword(1L));
  }

  private static User buildUser(
      Long id, String username, String passwordHash, String nickname, UserStatus status) {
    return User.restore(
        UserId.of(id),
        username,
        passwordHash,
        nickname,
        null,
        null,
        null,
        null,
        status,
        1,
        null,
        null,
        null,
        0);
  }
}

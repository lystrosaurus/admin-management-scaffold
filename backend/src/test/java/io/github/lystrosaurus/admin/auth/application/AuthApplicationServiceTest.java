package io.github.lystrosaurus.admin.auth.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.adapter.response.LoginResponse;
import io.github.lystrosaurus.admin.auth.application.command.LoginCommand;
import io.github.lystrosaurus.admin.auth.domain.acl.ClientRequestInfoPort;
import io.github.lystrosaurus.admin.auth.domain.acl.TokenSessionPort;
import io.github.lystrosaurus.admin.auth.domain.event.LoginSucceeded;
import io.github.lystrosaurus.admin.shared.kernel.exception.BusinessException;
import io.github.lystrosaurus.admin.shared.kernel.exception.ErrorCode;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.domain.model.UserStatus;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** AuthApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthApplicationService 测试")
class AuthApplicationServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private BCryptPasswordEncoder passwordEncoder;

  @Mock private DomainEventPublisher domainEventPublisher;

  @Mock private RedisTemplate<String, Object> redisTemplate;

  @Mock private ValueOperations<String, Object> valueOperations;

  @Mock private TokenSessionPort tokenSessionPort;

  @Mock private ClientRequestInfoPort clientRequestInfoPort;

  @InjectMocks private AuthApplicationService authApplicationService;

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
    LoginCommand loginRequest = new LoginCommand("nonexistent", "password123");
    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> authApplicationService.login(loginRequest));
    assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该抛出异常当用户被禁用")
  void should_throw_exception_when_user_disabled() {
    LoginCommand loginRequest = new LoginCommand("disabled", "password123");
    when(userRepository.findByUsername("disabled")).thenReturn(Optional.of(disabledUser));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> authApplicationService.login(loginRequest));
    assertEquals(ErrorCode.USER_ACCOUNT_DISABLED.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该抛出异常当密码错误")
  void should_throw_exception_when_password_mismatch() {
    LoginCommand loginRequest = new LoginCommand("admin", "wrong-password");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(activeUser));
    when(passwordEncoder.matches("wrong-password", activeUser.passwordHash())).thenReturn(false);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> authApplicationService.login(loginRequest));
    assertEquals(ErrorCode.USER_INVALID_PASSWORD.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功登录并发布登录成功事件")
  void should_login_successfully_and_publish_login_succeeded() {
    LoginCommand loginRequest = new LoginCommand("admin", "password123");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(activeUser));
    when(passwordEncoder.matches("password123", activeUser.passwordHash())).thenReturn(true);
    when(clientRequestInfoPort.clientIp()).thenReturn("127.0.0.1");
    when(clientRequestInfoPort.userAgent()).thenReturn("Chrome");
    when(tokenSessionPort.currentToken()).thenReturn("jwt-token-123");
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    LoginResponse response = authApplicationService.login(loginRequest);

    assertNotNull(response);
    assertEquals("jwt-token-123", response.accessToken());
    assertEquals("admin", response.user().username());
    verify(tokenSessionPort).login(1L);
    ArgumentCaptor<LoginSucceeded> captor = ArgumentCaptor.forClass(LoginSucceeded.class);
    verify(domainEventPublisher).publish(captor.capture());
    LoginSucceeded event = captor.getValue();
    assertEquals(1L, event.userId());
    assertEquals("PASSWORD", event.loginType());
    assertNull(event.providerCode());
    assertEquals("127.0.0.1", event.ipAddress());
    assertEquals("Chrome", event.userAgent());
    assertNotNull(event.occurredAt());
    verify(userRepository).save(activeUser);
  }

  @Test
  @DisplayName("应该正确判断用户是否设置了密码")
  void should_correctly_check_if_user_has_password() {
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(activeUser));
    assertTrue(authApplicationService.hasPassword(1L));

    User noPasswordUser = buildUser(1L, "admin", null, "管理员", UserStatus.ENABLED);
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(noPasswordUser));
    assertFalse(authApplicationService.hasPassword(1L));
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

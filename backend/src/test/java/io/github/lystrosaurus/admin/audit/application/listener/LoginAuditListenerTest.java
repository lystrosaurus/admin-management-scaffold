package io.github.lystrosaurus.admin.audit.application.listener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.audit.application.LoginLogApplicationService;
import io.github.lystrosaurus.admin.audit.domain.acl.LoginLogRepository;
import io.github.lystrosaurus.admin.audit.domain.model.LoginLog;
import io.github.lystrosaurus.admin.auth.domain.event.LoginSucceeded;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** 登录审计监听者单元测试 */
@DisplayName("登录审计监听者测试")
class LoginAuditListenerTest {

  private LoginLogRepository loginLogRepository;
  private LoginAuditListener listener;

  @BeforeEach
  void setUp() {
    loginLogRepository = mock(LoginLogRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    listener =
        new LoginAuditListener(
            new LoginLogApplicationService(loginLogRepository, userRepository));
  }

  @Test
  @DisplayName("密码登录事件应按 SUCCESS 状态落库并保留事件字段")
  void should_record_success_log_from_password_event() {
    LocalDateTime occurredAt = LocalDateTime.of(2025, 1, 1, 10, 0);

    listener.onLoginSucceeded(
        new LoginSucceeded(7L, "PASSWORD", null, "10.0.0.1", "Chrome", occurredAt));

    ArgumentCaptor<LoginLog> captor = ArgumentCaptor.forClass(LoginLog.class);
    verify(loginLogRepository).save(captor.capture());
    LoginLog saved = captor.getValue();
    assertNull(saved.id());
    assertEquals(7L, saved.userId());
    assertEquals("PASSWORD", saved.loginType());
    assertNull(saved.providerCode());
    assertEquals("10.0.0.1", saved.ipAddress());
    assertEquals("Chrome", saved.userAgent());
    assertEquals("SUCCESS", saved.status());
    assertNull(saved.failureReason());
    assertEquals(occurredAt, saved.loginAt());
  }

  @Test
  @DisplayName("OAuth 登录事件应保留提供方编码且忽略空 IP/UA")
  void should_record_success_log_from_oauth_event() {
    LocalDateTime occurredAt = LocalDateTime.of(2025, 1, 1, 11, 0);

    listener.onLoginSucceeded(new LoginSucceeded(9L, "OAUTH", "LARK", null, null, occurredAt));

    ArgumentCaptor<LoginLog> captor = ArgumentCaptor.forClass(LoginLog.class);
    verify(loginLogRepository).save(captor.capture());
    LoginLog saved = captor.getValue();
    assertEquals(9L, saved.userId());
    assertEquals("OAUTH", saved.loginType());
    assertEquals("LARK", saved.providerCode());
    assertNull(saved.ipAddress());
    assertNull(saved.userAgent());
    assertEquals("SUCCESS", saved.status());
    assertEquals(occurredAt, saved.loginAt());
  }

  @Test
  @DisplayName("监听方法应在事务提交后执行且无事务时回退执行")
  void should_listen_after_commit_with_fallback_execution() throws NoSuchMethodException {
    Method method =
        LoginAuditListener.class.getMethod("onLoginSucceeded", LoginSucceeded.class);
    TransactionalEventListener annotation =
        method.getAnnotation(TransactionalEventListener.class);

    assertNotNull(annotation);
    assertEquals(TransactionPhase.AFTER_COMMIT, annotation.phase());
    assertTrue(annotation.fallbackExecution());
  }
}

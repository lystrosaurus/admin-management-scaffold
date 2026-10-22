package io.github.lystrosaurus.admin.audit.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.audit.adapter.request.LoginLogQueryRequest;
import io.github.lystrosaurus.admin.audit.adapter.response.LoginLogResponse;
import io.github.lystrosaurus.admin.audit.domain.acl.LoginLogRepository;
import io.github.lystrosaurus.admin.audit.domain.model.LoginLog;
import io.github.lystrosaurus.admin.auth.domain.event.LoginSucceeded;
import io.github.lystrosaurus.admin.shared.adapter.web.PageResult;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** LoginLogApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("登录日志应用服务测试")
class LoginLogApplicationServiceTest {

  @Mock private LoginLogRepository loginLogRepository;
  @Mock private UserRepository userRepository;

  @InjectMocks private LoginLogApplicationService loginLogApplicationService;

  @Test
  @DisplayName("应该成功记录登录日志")
  void should_record_login_log() {
    LocalDateTime occurredAt = LocalDateTime.of(2025, 1, 1, 10, 0);

    loginLogApplicationService.recordLogin(
        new LoginSucceeded(1L, "PASSWORD", null, "127.0.0.1", "Chrome", occurredAt));

    ArgumentCaptor<LoginLog> captor = ArgumentCaptor.forClass(LoginLog.class);
    verify(loginLogRepository).save(captor.capture());
    LoginLog saved = captor.getValue();
    assertEquals(1L, saved.userId());
    assertEquals("PASSWORD", saved.loginType());
    assertNull(saved.providerCode());
    assertEquals("127.0.0.1", saved.ipAddress());
    assertEquals("Chrome", saved.userAgent());
    assertEquals("SUCCESS", saved.status());
    assertNull(saved.failureReason());
    assertEquals(occurredAt, saved.loginAt());
  }

  @Test
  @DisplayName("应该成功获取最近登录日志并映射为前端契约字段")
  void should_get_recent_login_logs() {
    LoginLog loginLog =
        LoginLog.restore(
            1L,
            1L,
            "PASSWORD",
            null,
            "127.0.0.1",
            "Chrome",
            "SUCCESS",
            null,
            LocalDateTime.now());
    when(loginLogRepository.listByUserId(1L, 10)).thenReturn(List.of(loginLog));

    User user = mock(User.class);
    when(user.username()).thenReturn("admin");
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(user));

    List<LoginLogResponse> result = loginLogApplicationService.getRecentLogins(1L, 10);

    assertNotNull(result);
    assertEquals(1, result.size());
    LoginLogResponse response = result.get(0);
    assertEquals("admin", response.username());
    assertEquals("127.0.0.1", response.ip());
    assertEquals(true, response.success());
    assertEquals("Chrome", response.userAgent());
    verify(loginLogRepository).listByUserId(1L, 10);
  }

  @Test
  @DisplayName("用户名无法解析时应返回空分页且不查询日志")
  void should_return_empty_page_when_username_not_found() {
    when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

    PageResult<LoginLogResponse> result =
        loginLogApplicationService.listLoginLogs(
            new LoginLogQueryRequest("missing", null, null, null, null, 2, 20));

    assertEquals(0, result.total());
    assertTrue(result.items().isEmpty());
    assertEquals(2, result.page());
    assertEquals(20, result.size());
    verifyNoInteractions(loginLogRepository);
  }
}

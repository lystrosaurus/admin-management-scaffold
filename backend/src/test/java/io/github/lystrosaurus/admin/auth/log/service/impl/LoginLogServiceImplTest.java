package io.github.lystrosaurus.admin.auth.log.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.log.dao.LoginLogDAO;
import io.github.lystrosaurus.admin.auth.log.entity.LoginLog;
import io.github.lystrosaurus.admin.auth.log.vo.LoginLogVO;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** LoginLogServiceImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginLogServiceImpl 测试")
class LoginLogServiceImplTest {

  @Mock private LoginLogDAO loginLogDAO;

  @InjectMocks private LoginLogServiceImpl loginLogService;

  @Test
  @DisplayName("应该成功记录登录日志")
  void should_record_login_successfully() {
    // Given
    Long userId = 1L;
    String loginType = "PASSWORD";
    String providerCode = null;
    String ipAddress = "127.0.0.1";
    String userAgent = "Mozilla/5.0";
    boolean success = true;
    String failureReason = null;

    // When
    loginLogService.recordLogin(
        userId, loginType, providerCode, ipAddress, userAgent, success, failureReason);

    // Then
    ArgumentCaptor<LoginLog> captor = ArgumentCaptor.forClass(LoginLog.class);
    verify(loginLogDAO).save(captor.capture());
    LoginLog captured = captor.getValue();
    assertEquals(userId, captured.getUserId());
    assertEquals(loginType, captured.getLoginType());
    assertNull(captured.getProviderCode());
    assertEquals(ipAddress, captured.getIpAddress());
    assertEquals(userAgent, captured.getUserAgent());
    assertEquals("SUCCESS", captured.getStatus());
    assertNull(captured.getFailureReason());
    assertNotNull(captured.getLoginAt());
  }

  @Test
  @DisplayName("记录登录日志时失败状态应该正确设置")
  void should_set_failed_status_when_login_failed() {
    // Given
    Long userId = 1L;
    String loginType = "PASSWORD";
    String providerCode = null;
    String ipAddress = "127.0.0.1";
    String userAgent = "Mozilla/5.0";
    boolean success = false;
    String failureReason = "密码错误";

    // When
    loginLogService.recordLogin(
        userId, loginType, providerCode, ipAddress, userAgent, success, failureReason);

    // Then
    ArgumentCaptor<LoginLog> captor = ArgumentCaptor.forClass(LoginLog.class);
    verify(loginLogDAO).save(captor.capture());
    LoginLog captured = captor.getValue();
    assertEquals("FAILED", captured.getStatus());
    assertEquals("密码错误", captured.getFailureReason());
  }

  @Test
  @DisplayName("记录OAuth登录日志时providerCode应该正确设置")
  void should_set_provider_code_for_oauth_login() {
    // Given
    Long userId = 1L;
    String loginType = "OAUTH_LARK";
    String providerCode = "LARK";
    String ipAddress = "127.0.0.1";
    String userAgent = "Mozilla/5.0";
    boolean success = true;
    String failureReason = null;

    // When
    loginLogService.recordLogin(
        userId, loginType, providerCode, ipAddress, userAgent, success, failureReason);

    // Then
    ArgumentCaptor<LoginLog> captor = ArgumentCaptor.forClass(LoginLog.class);
    verify(loginLogDAO).save(captor.capture());
    LoginLog captured = captor.getValue();
    assertEquals("OAUTH_LARK", captured.getLoginType());
    assertEquals("LARK", captured.getProviderCode());
  }

  @Test
  @DisplayName("应该成功获取用户最近登录记录")
  void should_get_recent_logins() {
    // Given
    Long userId = 1L;
    int limit = 5;
    LoginLog log1 = new LoginLog();
    log1.setId(1L);
    log1.setUserId(userId);
    log1.setLoginType("PASSWORD");
    log1.setStatus("SUCCESS");
    log1.setLoginAt(LocalDateTime.now().minusHours(1));

    LoginLog log2 = new LoginLog();
    log2.setId(2L);
    log2.setUserId(userId);
    log2.setLoginType("OAUTH_LARK");
    log2.setStatus("SUCCESS");
    log2.setLoginAt(LocalDateTime.now());

    when(loginLogDAO.listByUserId(userId, limit)).thenReturn(Arrays.asList(log2, log1));

    // When
    List<LoginLogVO> result = loginLogService.getRecentLogins(userId, limit);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(2L, result.get(0).id());
    assertEquals("OAUTH_LARK", result.get(0).loginType());
    assertEquals(1L, result.get(1).id());
    assertEquals("PASSWORD", result.get(1).loginType());
    verify(loginLogDAO).listByUserId(userId, limit);
  }
}

package io.github.lystrosaurus.admin.auth.log.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.log.dao.LoginLogDAO;
import io.github.lystrosaurus.admin.auth.log.entity.LoginLog;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** LoginLogService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("登录日志服务测试")
class LoginLogServiceTest {

  @Mock private LoginLogDAO loginLogDAO;

  @InjectMocks private LoginLogService loginLogService;

  @Test
  @DisplayName("应该成功记录登录日志")
  void should_record_login_log() {
    loginLogService.recordLogin(1L, "PASSWORD", null, "127.0.0.1", "Chrome", true, null);
    verify(loginLogDAO).save(any(LoginLog.class));
  }

  @Test
  @DisplayName("应该成功获取最近登录日志")
  void should_get_recent_login_logs() {
    LoginLog loginLog = new LoginLog();
    loginLog.setId(1L);
    loginLog.setUserId(1L);
    loginLog.setLoginType("PASSWORD");
    loginLog.setStatus("SUCCESS");
    loginLog.setLoginAt(LocalDateTime.now());
    when(loginLogDAO.listByUserId(1L, 10)).thenReturn(Arrays.asList(loginLog));

    List<io.github.lystrosaurus.admin.auth.log.vo.LoginLogVO> result =
        loginLogService.getRecentLogins(1L, 10);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(loginLogDAO).listByUserId(1L, 10);
  }
}

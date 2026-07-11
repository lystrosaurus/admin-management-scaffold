package io.github.lystrosaurus.admin.auth.log.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.log.dao.LoginLogDAO;
import io.github.lystrosaurus.admin.auth.log.entity.LoginLog;
import io.github.lystrosaurus.admin.auth.log.vo.LoginLogVO;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
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
  @Mock private UserDAO userDAO;

  @InjectMocks private LoginLogService loginLogService;

  @Test
  @DisplayName("应该成功记录登录日志")
  void should_record_login_log() {
    loginLogService.recordLogin(1L, "PASSWORD", null, "127.0.0.1", "Chrome", true, null);
    verify(loginLogDAO).save(any(LoginLog.class));
  }

  @Test
  @DisplayName("应该成功获取最近登录日志并映射为前端契约字段")
  void should_get_recent_login_logs() {
    LoginLog loginLog = new LoginLog();
    loginLog.setId(1L);
    loginLog.setUserId(1L);
    loginLog.setLoginType("PASSWORD");
    loginLog.setStatus("SUCCESS");
    loginLog.setIpAddress("127.0.0.1");
    loginLog.setUserAgent("Chrome");
    loginLog.setLoginAt(LocalDateTime.now());
    when(loginLogDAO.listByUserId(1L, 10)).thenReturn(Arrays.asList(loginLog));

    SysUser user = mock(SysUser.class);
    when(user.getUsername()).thenReturn("admin");
    when(userDAO.findById(1L)).thenReturn(user);

    List<LoginLogVO> result = loginLogService.getRecentLogins(1L, 10);

    assertNotNull(result);
    assertEquals(1, result.size());
    LoginLogVO vo = result.get(0);
    assertEquals("admin", vo.username());
    assertEquals("127.0.0.1", vo.ip());
    assertEquals(true, vo.success());
    assertEquals("Chrome", vo.userAgent());
    verify(loginLogDAO).listByUserId(1L, 10);
  }
}

package io.github.lystrosaurus.admin.audit.application;

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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** 登录日志应用服务：记录登录事件并支撑登录日志查询。 */
@Service
@RequiredArgsConstructor
public class LoginLogApplicationService {

  private final LoginLogRepository loginLogRepository;
  private final UserRepository userRepository;

  /** 记录登录成功事件 */
  public void recordLogin(LoginSucceeded event) {
    LoginLog log =
        LoginLog.record(
            event.userId(),
            event.loginType(),
            event.providerCode(),
            event.ipAddress(),
            event.userAgent(),
            "SUCCESS",
            null,
            event.occurredAt());
    loginLogRepository.save(log);
  }

  /** 查询用户最近的登录记录 */
  public List<LoginLogResponse> getRecentLogins(Long userId, int limit) {
    return loginLogRepository.listByUserId(userId, limit).stream()
        .map(this::toResponse)
        .toList();
  }

  /** 条件分页查询登录日志 */
  public PageResult<LoginLogResponse> listLoginLogs(LoginLogQueryRequest request) {
    Long userId = resolveUserId(request.username());
    if (StringUtils.hasText(request.username()) && userId == null) {
      return new PageResult<>(List.of(), 0, request.page(), request.size());
    }
    Page<LoginLog> page =
        loginLogRepository.findPage(
            userId,
            request.ipAddress(),
            request.status(),
            request.start(),
            request.end(),
            request.page(),
            request.size());
    List<LoginLogResponse> responses = page.records().stream().map(this::toResponse).toList();
    return new PageResult<>(responses, page.total(), request.page(), request.size());
  }

  private LoginLogResponse toResponse(LoginLog log) {
    return new LoginLogResponse(
        log.id(),
        resolveUsername(log.userId()),
        log.ipAddress(),
        "SUCCESS".equals(log.status()),
        log.loginAt(),
        log.userAgent());
  }

  private Long resolveUserId(String username) {
    if (!StringUtils.hasText(username)) {
      return null;
    }
    return userRepository.findByUsername(username).map(user -> user.id().value()).orElse(null);
  }

  /** userId → username（用于日志展示），未知或解析失败返回 null */
  private String resolveUsername(Long userId) {
    if (userId == null) {
      return null;
    }
    return userRepository.findById(UserId.of(userId)).map(User::username).orElse(null);
  }
}

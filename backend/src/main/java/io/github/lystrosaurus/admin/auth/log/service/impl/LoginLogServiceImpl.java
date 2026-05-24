package io.github.lystrosaurus.admin.auth.log.service.impl;

import io.github.lystrosaurus.admin.auth.log.dao.LoginLogDAO;
import io.github.lystrosaurus.admin.auth.log.entity.LoginLog;
import io.github.lystrosaurus.admin.auth.log.service.LoginLogService;
import io.github.lystrosaurus.admin.auth.log.vo.LoginLogVO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 登录日志服务实现 */
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

  private final LoginLogDAO loginLogDAO;

  @Override
  public void recordLogin(
      Long userId,
      String loginType,
      String providerCode,
      String ipAddress,
      String userAgent,
      boolean success,
      String failureReason) {
    LoginLog log = new LoginLog();
    log.setUserId(userId);
    log.setLoginType(loginType);
    log.setProviderCode(providerCode);
    log.setIpAddress(ipAddress);
    log.setUserAgent(userAgent);
    log.setStatus(success ? "SUCCESS" : "FAILED");
    log.setFailureReason(failureReason);
    log.setLoginAt(LocalDateTime.now());
    loginLogDAO.save(log);
  }

  @Override
  public List<LoginLogVO> getRecentLogins(Long userId, int limit) {
    return loginLogDAO.listByUserId(userId, limit).stream()
        .map(
            log ->
                new LoginLogVO(
                    log.getId(),
                    log.getLoginType(),
                    log.getProviderCode(),
                    log.getIpAddress(),
                    log.getStatus(),
                    log.getLoginAt()))
        .toList();
  }
}

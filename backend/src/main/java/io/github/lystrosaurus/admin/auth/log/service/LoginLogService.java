package io.github.lystrosaurus.admin.auth.log.service;

import io.github.lystrosaurus.admin.auth.log.dao.LoginLogDAO;
import io.github.lystrosaurus.admin.auth.log.dto.LoginLogQueryDTO;
import io.github.lystrosaurus.admin.auth.log.entity.LoginLog;
import io.github.lystrosaurus.admin.auth.log.vo.LoginLogVO;
import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** 登录日志服务实现 */
@Service
@RequiredArgsConstructor
public class LoginLogService {

  private final LoginLogDAO loginLogDAO;
  private final UserDAO userDAO;

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

  public List<LoginLogVO> getRecentLogins(Long userId, int limit) {
    return loginLogDAO.listByUserId(userId, limit).stream()
        .map(
            log ->
                new LoginLogVO(
                    log.getId(),
                    resolveUsername(log.getUserId()),
                    log.getIpAddress(),
                    "SUCCESS".equals(log.getStatus()),
                    log.getLoginAt(),
                    log.getUserAgent()))
        .toList();
  }

  /** 条件分页查询登录日志 */
  public PageResult<LoginLogVO> listLoginLogs(LoginLogQueryDTO dto) {
    Long userId = resolveUserId(dto.username());
    if (StringUtils.hasText(dto.username()) && userId == null) {
      return new PageResult<>(List.of(), 0, dto.page(), dto.size());
    }
    var page =
        loginLogDAO.findPage(
            userId, dto.ipAddress(), dto.status(), dto.start(), dto.end(), dto.page(), dto.size());
    List<LoginLogVO> vos =
        page.getRecords().stream()
            .map(
                log ->
                    new LoginLogVO(
                        log.getId(),
                        resolveUsername(log.getUserId()),
                        log.getIpAddress(),
                        "SUCCESS".equals(log.getStatus()),
                        log.getLoginAt(),
                        log.getUserAgent()))
            .toList();
    return new PageResult<>(vos, page.getTotal(), dto.page(), dto.size());
  }

  private Long resolveUserId(String username) {
    if (!StringUtils.hasText(username)) {
      return null;
    }
    var user = userDAO.findByUsername(username);
    return user == null ? null : user.getId();
  }

  /** userId → username（用于日志展示），未知或解析失败返回 null */
  private String resolveUsername(Long userId) {
    if (userId == null) {
      return null;
    }
    var user = userDAO.findById(userId);
    return user == null ? null : user.getUsername();
  }
}

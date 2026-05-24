package io.github.lystrosaurus.admin.auth.controller;

import io.github.lystrosaurus.admin.auth.context.UserContext;
import io.github.lystrosaurus.admin.auth.external.service.ExternalAccountService;
import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import io.github.lystrosaurus.admin.auth.log.entity.LoginLog;
import io.github.lystrosaurus.admin.auth.log.service.LoginLogService;
import io.github.lystrosaurus.admin.auth.log.vo.LoginLogVO;
import io.github.lystrosaurus.admin.auth.vo.SecurityProfileVO;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账号安全信息控制器
 *
 * <p>提供当前登录用户的安全信息查询接口，包括密码状态、绑定的三方账号和最近登录记录。
 */
@RestController
@RequestMapping("/app/profile")
@RequiredArgsConstructor
public class SecurityProfileController {

  private final UserDAO userDAO;
  private final ExternalAccountService externalAccountService;
  private final LoginLogService loginLogService;

  /**
   * 获取账号安全信息
   *
   * @return 安全信息VO
   */
  @GetMapping("/security")
  public ApiResponse<SecurityProfileVO> getSecurityProfile() {
    Long userId = UserContext.getCurrentUserId();

    // 检查是否有密码
    SysUser user = userDAO.findById(userId);
    boolean hasPassword =
        user != null && user.getPasswordHash() != null && !user.getPasswordHash().isBlank();

    // 查询绑定的三方账号
    List<ExternalAccountVO> boundAccounts = externalAccountService.listByUserId(userId);

    // 查询最近登录记录
    List<LoginLog> loginLogs = loginLogService.getRecentLogins(userId, 10);
    List<LoginLogVO> recentLogins =
        loginLogs.stream()
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

    return ApiResponse.success(new SecurityProfileVO(hasPassword, boundAccounts, recentLogins));
  }
}

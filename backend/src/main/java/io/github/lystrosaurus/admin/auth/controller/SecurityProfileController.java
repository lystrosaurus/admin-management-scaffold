package io.github.lystrosaurus.admin.auth.controller;

import io.github.lystrosaurus.admin.auth.context.UserContext;
import io.github.lystrosaurus.admin.auth.external.service.ExternalAccountService;
import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import io.github.lystrosaurus.admin.auth.log.service.LoginLogService;
import io.github.lystrosaurus.admin.auth.log.vo.LoginLogVO;
import io.github.lystrosaurus.admin.auth.service.AuthService;
import io.github.lystrosaurus.admin.auth.vo.SecurityProfileVO;
import io.github.lystrosaurus.admin.common.ApiResponse;
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

  private final AuthService authService;
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

    boolean hasPassword = authService.hasPassword(userId);
    List<ExternalAccountVO> boundAccounts = externalAccountService.listByUserId(userId);
    List<LoginLogVO> recentLogins = loginLogService.getRecentLogins(userId, 10);

    return ApiResponse.success(new SecurityProfileVO(hasPassword, boundAccounts, recentLogins));
  }
}

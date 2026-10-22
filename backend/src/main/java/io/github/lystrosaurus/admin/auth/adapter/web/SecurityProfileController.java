package io.github.lystrosaurus.admin.auth.adapter.web;

import io.github.lystrosaurus.admin.auth.adapter.response.SecurityProfileResponse;
import io.github.lystrosaurus.admin.auth.annotation.PermitAll;
import io.github.lystrosaurus.admin.auth.application.ProfileApplicationService;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
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

  private final ProfileApplicationService profileApplicationService;

  /**
   * 获取账号安全信息
   *
   * @return 安全信息响应
   */
  @PermitAll(reason = "当前登录用户的安全信息，只读自身")
  @GetMapping("/security")
  public ApiResponse<SecurityProfileResponse> getSecurityProfile() {
    return ApiResponse.success(profileApplicationService.getSecurityProfile());
  }
}

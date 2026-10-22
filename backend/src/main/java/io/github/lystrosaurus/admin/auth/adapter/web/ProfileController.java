package io.github.lystrosaurus.admin.auth.adapter.web;

import io.github.lystrosaurus.admin.auth.adapter.response.ProfileResponse;
import io.github.lystrosaurus.admin.auth.annotation.PermitAll;
import io.github.lystrosaurus.admin.auth.application.ProfileApplicationService;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.shared.annotation.RateLimit;
import io.github.lystrosaurus.admin.system.user.adapter.request.ChangePasswordRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户资料控制器
 *
 * <p>提供当前登录用户资料相关接口
 */
@RestController
@RequestMapping("/app/profile")
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileApplicationService profileApplicationService;

  /**
   * 获取当前用户资料
   *
   * @return 用户资料响应
   */
  @PermitAll(reason = "当前登录用户的资料，只读自身")
  @GetMapping
  public ApiResponse<ProfileResponse> getProfile() {
    return ApiResponse.success(profileApplicationService.getCurrentProfile());
  }

  /**
   * 修改自己的密码
   *
   * @param request 修改密码请求
   * @return 空响应
   */
  @PermitAll(reason = "修改当前登录用户自身密码")
  @RateLimit(prefix = "profile:change-password", limit = 5, windowSeconds = 60, byClientIp = true)
  @PutMapping("/password")
  public ApiResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
    profileApplicationService.changePassword(request.toCommand());
    return ApiResponse.success();
  }

  /**
   * 获取当前用户权限码列表
   *
   * @return 权限码列表
   */
  @PermitAll(reason = "查看当前登录用户自身权限码")
  @GetMapping("/permissions")
  public ApiResponse<List<String>> getPermissions() {
    return ApiResponse.success(profileApplicationService.getCurrentPermissions());
  }
}

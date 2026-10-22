package io.github.lystrosaurus.admin.auth.adapter.web;

import io.github.lystrosaurus.admin.auth.adapter.request.OAuthBindRequest;
import io.github.lystrosaurus.admin.auth.adapter.response.ExternalAccountResponse;
import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.auth.application.OAuthApplicationService;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.shared.annotation.Idempotent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth 绑定/解绑控制器
 *
 * <p>提供当前登录用户绑定和解绑三方账号的接口。
 */
@RestController
@RequestMapping("/app/auth/oauth")
@RequiredArgsConstructor
public class OAuthBindController {

  private final OAuthApplicationService oauthApplicationService;

  /**
   * 绑定三方账号
   *
   * @param provider 认证源编码
   * @param request 绑定参数（code + state）
   * @return 绑定的三方账号信息
   */
  @RequiresPermission("auth:external-account:bind")
  @Idempotent(prefix = "auth:bind", ttlSeconds = 10)
  @PostMapping("/{provider}/bind")
  public ApiResponse<ExternalAccountResponse> bind(
      @PathVariable String provider, @RequestBody OAuthBindRequest request) {
    ExternalAccountResponse result =
        oauthApplicationService.bindAccount(provider, request.code(), request.state());
    return ApiResponse.success(result);
  }

  /**
   * 解绑三方账号
   *
   * @param provider 认证源编码
   * @param accountId 三方账号ID
   * @return 空响应
   */
  @RequiresPermission("auth:external-account:unbind")
  @DeleteMapping("/{provider}/unbind")
  public ApiResponse<Void> unbind(@PathVariable String provider, @RequestParam Long accountId) {
    oauthApplicationService.unbindAccount(provider, accountId);
    return ApiResponse.success();
  }
}

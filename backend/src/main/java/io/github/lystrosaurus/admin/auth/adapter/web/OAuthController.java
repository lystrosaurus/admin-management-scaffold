package io.github.lystrosaurus.admin.auth.adapter.web;

import io.github.lystrosaurus.admin.auth.adapter.request.OAuthCallbackRequest;
import io.github.lystrosaurus.admin.auth.adapter.response.OAuthAuthorizeResponse;
import io.github.lystrosaurus.admin.auth.adapter.response.OAuthLoginResponse;
import io.github.lystrosaurus.admin.auth.application.OAuthApplicationService;
import io.github.lystrosaurus.admin.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** OAuth 公开控制器（无需登录） */
@RestController
@RequestMapping("/public/oauth")
@RequiredArgsConstructor
public class OAuthController {

  private final OAuthApplicationService oauthApplicationService;

  /**
   * 发起 OAuth 授权
   *
   * @param provider 认证源编码
   * @param redirectBase 前端实际访问源
   * @return 授权 URL 和 state
   */
  @GetMapping("/{provider}/authorize")
  public ApiResponse<OAuthAuthorizeResponse> authorize(
      @PathVariable String provider,
      @RequestParam(required = false) String redirectBase) {
    OAuthAuthorizeResponse result = oauthApplicationService.authorize(provider, redirectBase);
    return ApiResponse.success(result);
  }

  /**
   * 处理 OAuth 回调
   *
   * @param provider 认证源编码
   * @param request 回调参数（code + state）
   * @return 登录结果
   */
  @GetMapping("/{provider}/callback")
  public ApiResponse<OAuthLoginResponse> callback(
      @PathVariable String provider, OAuthCallbackRequest request) {
    OAuthLoginResponse result =
        oauthApplicationService.handleCallback(provider, request.code(), request.state());
    return ApiResponse.success(result);
  }
}

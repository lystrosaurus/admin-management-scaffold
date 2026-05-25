package io.github.lystrosaurus.admin.auth.oauth.controller;

import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthCallbackDTO;
import io.github.lystrosaurus.admin.auth.oauth.service.OAuthService;
import io.github.lystrosaurus.admin.auth.oauth.vo.OAuthAuthorizeVO;
import io.github.lystrosaurus.admin.auth.oauth.vo.OAuthLoginVO;
import io.github.lystrosaurus.admin.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** OAuth 公开控制器（无需登录） */
@RestController
@RequestMapping("/public/oauth")
@RequiredArgsConstructor
public class OAuthController {

  private final OAuthService oauthService;

  /**
   * 发起 OAuth 授权
   *
   * @param provider 认证源编码
   * @return 授权 URL 和 state
   */
  @GetMapping("/{provider}/authorize")
  public ApiResponse<OAuthAuthorizeVO> authorize(@PathVariable String provider) {
    OAuthAuthorizeVO result = oauthService.authorize(provider);
    return ApiResponse.success(result);
  }

  /**
   * 处理 OAuth 回调
   *
   * @param provider 认证源编码
   * @param dto 回调参数（code + state）
   * @return 登录结果
   */
  @GetMapping("/{provider}/callback")
  public ApiResponse<OAuthLoginVO> callback(@PathVariable String provider, OAuthCallbackDTO dto) {
    OAuthLoginVO result = oauthService.handleCallback(provider, dto);
    return ApiResponse.success(result);
  }
}

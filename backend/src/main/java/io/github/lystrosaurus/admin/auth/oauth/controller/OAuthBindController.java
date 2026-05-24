package io.github.lystrosaurus.admin.auth.oauth.controller;

import io.github.lystrosaurus.admin.auth.context.UserContext;
import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthBindDTO;
import io.github.lystrosaurus.admin.auth.oauth.service.OAuthService;
import io.github.lystrosaurus.admin.common.ApiResponse;
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

  private final OAuthService oauthService;

  /**
   * 绑定三方账号
   *
   * @param provider 认证源编码
   * @param dto 绑定参数（code + state）
   * @return 绑定的三方账号信息
   */
  @PostMapping("/{provider}/bind")
  public ApiResponse<ExternalAccountVO> bind(
      @PathVariable String provider, @RequestBody OAuthBindDTO dto) {
    Long userId = UserContext.getCurrentUserId();
    ExternalAccountVO result = oauthService.bindAccount(userId, provider, dto);
    return ApiResponse.success(result);
  }

  /**
   * 解绑三方账号
   *
   * @param provider 认证源编码
   * @param accountId 三方账号ID
   * @return 空响应
   */
  @DeleteMapping("/{provider}/unbind")
  public ApiResponse<Void> unbind(@PathVariable String provider, @RequestParam Long accountId) {
    Long userId = UserContext.getCurrentUserId();
    oauthService.unbindAccount(userId, provider, accountId);
    return ApiResponse.success();
  }
}

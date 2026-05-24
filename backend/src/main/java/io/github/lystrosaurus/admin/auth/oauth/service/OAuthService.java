package io.github.lystrosaurus.admin.auth.oauth.service;

import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthBindDTO;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthCallbackDTO;
import io.github.lystrosaurus.admin.auth.oauth.vo.OAuthAuthorizeVO;
import io.github.lystrosaurus.admin.auth.oauth.vo.OAuthLoginVO;

/**
 * OAuth 服务接口
 *
 * <p>提供 OAuth 授权、回调、绑定、解绑等功能。具体实现由 Agent A 提供。
 */
public interface OAuthService {

  /**
   * 发起 OAuth 授权
   *
   * @param providerCode 认证源编码
   * @return 授权 URL 和 state
   */
  OAuthAuthorizeVO authorize(String providerCode);

  /**
   * 处理 OAuth 回调
   *
   * @param providerCode 认证源编码
   * @param dto 回调参数（code + state）
   * @return 登录结果（token 或需要绑定）
   */
  OAuthLoginVO handleCallback(String providerCode, OAuthCallbackDTO dto);

  /**
   * 绑定三方账号到当前用户
   *
   * @param userId 本地用户ID
   * @param providerCode 认证源编码
   * @param dto 绑定参数（code + state）
   * @return 绑定的三方账号信息
   */
  ExternalAccountVO bindAccount(Long userId, String providerCode, OAuthBindDTO dto);

  /**
   * 解绑三方账号
   *
   * @param userId 本地用户ID（校验归属）
   * @param providerCode 认证源编码
   * @param accountId 三方账号ID
   */
  void unbindAccount(Long userId, String providerCode, Long accountId);
}

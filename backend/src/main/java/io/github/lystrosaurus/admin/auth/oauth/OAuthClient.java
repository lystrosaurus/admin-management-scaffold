package io.github.lystrosaurus.admin.auth.oauth;

import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthTokenResponse;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthUserInfo;

/** OAuth 客户端接口 */
public interface OAuthClient {

  /**
   * 获取提供方编码
   *
   * @return 提供方编码
   */
  String getProviderCode();

  /**
   * 构建授权 URL
   *
   * @param state 状态值
   * @param redirectUri 回调地址
   * @return 授权 URL
   */
  String buildAuthorizationUrl(String state, String redirectUri);

  /**
   * 用授权码换取 access_token
   *
   * @param code 授权码
   * @param redirectUri 回调地址
   * @return token 响应
   */
  OAuthTokenResponse exchangeCode(String code, String redirectUri);

  /**
   * 获取用户信息
   *
   * @param accessToken access_token
   * @return 用户信息
   */
  OAuthUserInfo getUserInfo(String accessToken);
}

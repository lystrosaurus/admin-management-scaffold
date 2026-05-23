package io.github.lystrosaurus.admin.auth.oauth;

import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthTokenResponse;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthUserInfo;
import org.springframework.stereotype.Component;

/** 微信 OAuth 客户端（占位实现） */
@Component("WECHAT")
public class WechatOAuthClient implements OAuthClient {

  @Override
  public String getProviderCode() {
    return "WECHAT";
  }

  @Override
  public String buildAuthorizationUrl(String state, String redirectUri) {
    throw new UnsupportedOperationException("WECHAT OAuth not implemented yet");
  }

  @Override
  public OAuthTokenResponse exchangeCode(String code, String redirectUri) {
    throw new UnsupportedOperationException("WECHAT OAuth not implemented yet");
  }

  @Override
  public OAuthUserInfo getUserInfo(String accessToken) {
    throw new UnsupportedOperationException("WECHAT OAuth not implemented yet");
  }
}

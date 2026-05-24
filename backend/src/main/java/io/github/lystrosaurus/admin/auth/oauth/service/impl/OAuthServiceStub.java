package io.github.lystrosaurus.admin.auth.oauth.service.impl;

import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthBindDTO;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthCallbackDTO;
import io.github.lystrosaurus.admin.auth.oauth.service.OAuthService;
import io.github.lystrosaurus.admin.auth.oauth.vo.OAuthAuthorizeVO;
import io.github.lystrosaurus.admin.auth.oauth.vo.OAuthLoginVO;
import org.springframework.stereotype.Service;

/**
 * OAuth 服务存根实现
 *
 * <p>占位实现，由 Agent A 提供完整功能。所有方法调用将抛出 UnsupportedOperationException。
 */
@Service
public class OAuthServiceStub implements OAuthService {

  @Override
  public OAuthAuthorizeVO authorize(String providerCode) {
    throw new UnsupportedOperationException("待 Agent A 实现");
  }

  @Override
  public OAuthLoginVO handleCallback(String providerCode, OAuthCallbackDTO dto) {
    throw new UnsupportedOperationException("待 Agent A 实现");
  }

  @Override
  public ExternalAccountVO bindAccount(Long userId, String providerCode, OAuthBindDTO dto) {
    throw new UnsupportedOperationException("待 Agent A 实现");
  }

  @Override
  public void unbindAccount(Long userId, String providerCode, Long accountId) {
    throw new UnsupportedOperationException("待 Agent A 实现");
  }
}

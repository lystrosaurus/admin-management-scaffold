package io.github.lystrosaurus.admin.auth.oauth;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.util.Map;
import org.springframework.stereotype.Component;

/** OAuth 客户端工厂 */
@Component
public class OAuthClientFactory {

  private final Map<String, OAuthClient> clients;

  public OAuthClientFactory(Map<String, OAuthClient> clients) {
    this.clients = clients;
  }

  /**
   * 获取 OAuth 客户端
   *
   * @param providerCode 提供方编码
   * @return OAuth 客户端
   * @throws BusinessException 提供方不存在时抛出 OAUTH_PROVIDER_NOT_FOUND
   */
  public OAuthClient getClient(String providerCode) {
    OAuthClient client = clients.get(providerCode);
    if (client == null) {
      throw new BusinessException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND);
    }
    return client;
  }
}

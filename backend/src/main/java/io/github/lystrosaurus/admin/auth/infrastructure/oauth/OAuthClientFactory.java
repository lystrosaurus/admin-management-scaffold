package io.github.lystrosaurus.admin.auth.infrastructure.oauth;

import io.github.lystrosaurus.admin.auth.domain.acl.OAuthClient;
import io.github.lystrosaurus.admin.auth.domain.acl.OAuthClientCatalog;
import io.github.lystrosaurus.admin.shared.kernel.exception.BusinessException;
import io.github.lystrosaurus.admin.shared.kernel.exception.ErrorCode;
import java.util.Map;
import org.springframework.stereotype.Component;

/** OAuth 客户端工厂：以 Spring Bean 名称作为认证源编码索引。 */
@Component
public class OAuthClientFactory implements OAuthClientCatalog {

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
  @Override
  public OAuthClient getClient(String providerCode) {
    OAuthClient client = clients.get(providerCode);
    if (client == null) {
      throw new BusinessException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND);
    }
    return client;
  }
}

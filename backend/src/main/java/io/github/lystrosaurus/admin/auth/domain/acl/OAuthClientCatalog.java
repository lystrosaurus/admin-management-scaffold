package io.github.lystrosaurus.admin.auth.domain.acl;

/** OAuth 客户端目录端口：按认证源编码获取对应客户端。 */
public interface OAuthClientCatalog {

  /**
   * 获取 OAuth 客户端
   *
   * @param providerCode 提供方编码
   * @return OAuth 客户端
   */
  OAuthClient getClient(String providerCode);
}

package io.github.lystrosaurus.admin.auth.domain.acl;

import io.github.lystrosaurus.admin.auth.domain.model.AuthProvider;
import java.util.Optional;

/** 认证源读取端口：供 OAuth 客户端与应用层按编码读取认证源配置。 */
public interface AuthProviderCatalog {

  /** 按编码查找认证源 */
  Optional<AuthProvider> findByCode(String code);
}

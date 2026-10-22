package io.github.lystrosaurus.admin.auth.domain.acl;

import io.github.lystrosaurus.admin.auth.domain.model.AuthProvider;
import java.util.List;
import java.util.Optional;

/** 认证源仓储端口：认证源的创建、更新、删除与查询契约。 */
public interface AuthProviderRepository {

  /** 按ID查找认证源 */
  Optional<AuthProvider> findById(Long id);

  /** 按编码查找认证源 */
  Optional<AuthProvider> findByCode(String code);

  /** 查询所有认证源 */
  List<AuthProvider> findAll();

  /** 保存认证源（新建或更新），并回读数据库填充的审计字段 */
  AuthProvider save(AuthProvider provider);

  /** 删除认证源（逻辑删除） */
  void deleteById(Long id);
}

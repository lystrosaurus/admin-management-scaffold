package io.github.lystrosaurus.admin.auth.domain.acl;

import io.github.lystrosaurus.admin.auth.domain.model.ExternalAccount;
import java.util.List;
import java.util.Optional;

/** 三方账号仓储端口。 */
public interface ExternalAccountRepository {

  /** 按ID查找三方账号 */
  Optional<ExternalAccount> findById(Long id);

  /** 按认证源ID和第三方用户ID查找 */
  Optional<ExternalAccount> findByProviderIdAndProviderUserId(
      Long providerId, String providerUserId);

  /** 按认证源ID和本地用户ID查找 */
  Optional<ExternalAccount> findByProviderIdAndUserId(Long providerId, Long userId);

  /** 新建三方账号，并回读数据库填充的审计字段 */
  ExternalAccount save(ExternalAccount account);

  /** 更新三方账号，并回读数据库填充的审计字段 */
  ExternalAccount update(ExternalAccount account);

  /** 按用户ID查询三方账号列表 */
  List<ExternalAccount> listByUserId(Long userId);

  /** 按员工ID查询三方账号列表 */
  List<ExternalAccount> listByEmployeeId(Long employeeId);

  /** 统计用户的有效绑定数（bindStatus=BOUND） */
  long countActiveBindsByUserId(Long userId);
}

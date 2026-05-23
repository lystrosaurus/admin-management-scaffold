package io.github.lystrosaurus.admin.auth.external.dao;

import io.github.lystrosaurus.admin.auth.external.entity.AuthExternalAccount;
import java.util.List;

/** 三方账号数据访问对象接口 */
public interface AuthExternalAccountDAO {

  /**
   * 保存三方账号
   *
   * @param account 三方账号实体
   */
  void save(AuthExternalAccount account);

  /**
   * 根据ID查找三方账号
   *
   * @param id 三方账号ID
   * @return 三方账号实体，不存在时返回null
   */
  AuthExternalAccount findById(Long id);

  /**
   * 根据认证源ID和第三方用户ID查找
   *
   * @param providerId 认证源ID
   * @param providerUserId 第三方用户ID
   * @return 三方账号实体，不存在时返回null
   */
  AuthExternalAccount findByProviderIdAndProviderUserId(Long providerId, String providerUserId);

  /**
   * 根据认证源ID和本地用户ID查找
   *
   * @param providerId 认证源ID
   * @param userId 本地用户ID
   * @return 三方账号实体，不存在时返回null
   */
  AuthExternalAccount findByProviderIdAndUserId(Long providerId, Long userId);

  /**
   * 更新三方账号
   *
   * @param account 三方账号实体
   */
  void updateById(AuthExternalAccount account);

  /**
   * 根据用户ID查询三方账号列表
   *
   * @param userId 用户ID
   * @return 三方账号列表
   */
  List<AuthExternalAccount> listByUserId(Long userId);

  /**
   * 根据员工ID查询三方账号列表
   *
   * @param employeeId 员工ID
   * @return 三方账号列表
   */
  List<AuthExternalAccount> listByEmployeeId(Long employeeId);
}

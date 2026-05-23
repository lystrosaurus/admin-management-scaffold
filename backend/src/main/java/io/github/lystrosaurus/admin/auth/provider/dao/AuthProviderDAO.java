package io.github.lystrosaurus.admin.auth.provider.dao;

import io.github.lystrosaurus.admin.auth.provider.entity.AuthProvider;
import java.util.List;

/** 认证源数据访问对象接口 */
public interface AuthProviderDAO {

  /**
   * 保存认证源
   *
   * @param provider 认证源实体
   */
  void save(AuthProvider provider);

  /**
   * 根据ID查找认证源
   *
   * @param id 认证源ID
   * @return 认证源实体，不存在时返回null
   */
  AuthProvider findById(Long id);

  /**
   * 根据编码查找认证源
   *
   * @param code 认证源编码
   * @return 认证源实体，不存在时返回null
   */
  AuthProvider findByCode(String code);

  /**
   * 更新认证源
   *
   * @param provider 认证源实体
   */
  void updateById(AuthProvider provider);

  /**
   * 删除认证源（逻辑删除）
   *
   * @param id 认证源ID
   */
  void deleteById(Long id);

  /**
   * 查询所有认证源
   *
   * @return 认证源列表
   */
  List<AuthProvider> listAll();
}

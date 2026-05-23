package io.github.lystrosaurus.admin.integration.principal.dao;

import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipalIdentifier;
import java.util.List;

/**
 * 外部主体标识符数据访问对象接口
 *
 * <p>定义 Service 需要的数据访问语义，而不是简单地暴露 Mapper 方法。
 */
public interface ExtPrincipalIdentifierDAO {

  /**
   * 根据外部主体ID查找标识符列表
   *
   * @param principalId 外部主体ID
   * @return 标识符列表
   */
  List<ExtPrincipalIdentifier> findByPrincipalId(Long principalId);

  /**
   * 保存标识符
   *
   * @param entity 标识符实体
   */
  void save(ExtPrincipalIdentifier entity);

  /**
   * 批量保存标识符
   *
   * @param entities 标识符实体列表
   */
  void saveBatch(List<ExtPrincipalIdentifier> entities);

  /**
   * 根据外部主体ID删除标识符
   *
   * @param principalId 外部主体ID
   */
  void deleteByPrincipalId(Long principalId);
}

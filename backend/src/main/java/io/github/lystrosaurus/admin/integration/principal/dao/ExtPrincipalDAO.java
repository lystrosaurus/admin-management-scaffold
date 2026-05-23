package io.github.lystrosaurus.admin.integration.principal.dao;

import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipal;

/**
 * 外部主体数据访问对象接口（最小版本）
 *
 * <p>仅包含 IdentityLinkCandidateService.confirm() 所需的方法。 完整版本将由 Agent A 提供。
 */
public interface ExtPrincipalDAO {

  /**
   * 根据ID更新外部主体
   *
   * @param principal 外部主体实体
   */
  void updateById(ExtPrincipal principal);

  /**
   * 根据ID查找外部主体
   *
   * @param id 外部主体ID
   * @return 外部主体实体，不存在时返回null
   */
  ExtPrincipal findById(Long id);
}

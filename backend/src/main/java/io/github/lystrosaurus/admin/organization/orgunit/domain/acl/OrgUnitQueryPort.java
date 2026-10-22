package io.github.lystrosaurus.admin.organization.orgunit.domain.acl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/** 组织单元查询端口：供 employee、数据权限等其它上下文读取组织信息。 */
public interface OrgUnitQueryPort {

  /** 按ID集合查询组织名称（组织ID → 名称） */
  Map<Long, String> findNamesByIds(Collection<Long> orgIds);

  /** 查询某个组织的全部下级组织ID（不含自身） */
  Set<Long> findDescendantIds(Long orgId);

  /** 统计组织单元总数 */
  long countAll();
}

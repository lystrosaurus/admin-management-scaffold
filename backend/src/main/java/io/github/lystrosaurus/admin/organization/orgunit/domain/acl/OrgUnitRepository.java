package io.github.lystrosaurus.admin.organization.orgunit.domain.acl;

import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnit;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnitId;
import io.github.lystrosaurus.admin.shared.domain.acl.GenericRepository;

/** 组织单元仓储端口：组织单元聚合的存取契约。 */
public interface OrgUnitRepository extends GenericRepository<OrgUnit, OrgUnitId> {

  /** 判断编码是否存在 */
  boolean existsByCode(String code);

  /** 判断编码是否存在且排除指定组织单元 */
  boolean existsByCodeAndIdNot(String code, OrgUnitId id);

  /** 判断是否存在子节点 */
  boolean hasChildren(Long id);
}

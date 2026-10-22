package io.github.lystrosaurus.admin.organization.orgunit.domain.acl;

import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnit;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** 组织单元查询端口：面向读模型的查询契约（CQRS 查询侧）。 */
public interface OrgUnitQueryRepository {

  /** 按编码查找组织单元 */
  Optional<OrgUnit> findByCode(String code);

  /** 按条件分页查询组织单元 */
  Page<OrgUnit> findPage(String keyword, String status, Long parentId, PageQuery pageQuery);

  /** 按条件统计组织单元数量 */
  long countByCondition(String keyword, String status, Long parentId);

  /** 查找所有未删除的组织单元（用于树构建） */
  List<OrgUnit> findAll();

  /** 根据父节点ID查找子节点 */
  List<OrgUnit> findByParentId(Long parentId);

  /** 按ID集合批量查找组织单元 */
  List<OrgUnit> findByIds(Collection<Long> ids);
}

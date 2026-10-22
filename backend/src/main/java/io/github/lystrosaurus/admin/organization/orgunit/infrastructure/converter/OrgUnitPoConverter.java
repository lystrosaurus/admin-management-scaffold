package io.github.lystrosaurus.admin.organization.orgunit.infrastructure.converter;

import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnit;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnitId;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence.po.OrgUnitPO;
import org.springframework.stereotype.Component;

/** 组织单元聚合 ↔ 持久化对象转换器。审计字段由 MyBatis-Plus 自动填充，不在此转换。 */
@Component
public class OrgUnitPoConverter {

  /** 领域对象转持久化对象 */
  public OrgUnitPO toPo(OrgUnit orgUnit) {
    OrgUnitPO po = new OrgUnitPO();
    po.setId(orgUnit.id() == null ? null : orgUnit.id().value());
    po.setParentId(orgUnit.parentId());
    po.setCode(orgUnit.code());
    po.setName(orgUnit.name());
    po.setFullPath(orgUnit.fullPath());
    po.setLevel(orgUnit.level());
    po.setManagerEmployeeId(orgUnit.managerEmployeeId());
    po.setSortOrder(orgUnit.sortOrder());
    po.setStatus(orgUnit.status());
    po.setSourceType(orgUnit.sourceType());
    po.setVersion(orgUnit.version());
    return po;
  }

  /** 持久化对象转领域对象（重建聚合，不登记事件） */
  public OrgUnit toDomain(OrgUnitPO po) {
    return OrgUnit.restore(
        OrgUnitId.of(po.getId()),
        po.getParentId(),
        po.getCode(),
        po.getName(),
        po.getFullPath(),
        po.getLevel(),
        po.getManagerEmployeeId(),
        po.getSortOrder(),
        po.getStatus(),
        po.getSourceType(),
        po.getCreatedAt(),
        po.getVersion());
  }
}

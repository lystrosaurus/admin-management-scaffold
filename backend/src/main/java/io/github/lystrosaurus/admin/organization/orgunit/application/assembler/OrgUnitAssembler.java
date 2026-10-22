package io.github.lystrosaurus.admin.organization.orgunit.application.assembler;

import io.github.lystrosaurus.admin.organization.orgunit.adapter.response.OrgUnitResponse;
import io.github.lystrosaurus.admin.organization.orgunit.adapter.response.OrgUnitTreeResponse;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnit;
import java.util.List;
import org.springframework.stereotype.Component;

/** 组织单元装配器：聚合 → 响应模型。 */
@Component
public class OrgUnitAssembler {

  /** 聚合转组织单元响应 */
  public OrgUnitResponse toResponse(OrgUnit orgUnit, String leaderName) {
    return new OrgUnitResponse(
        orgUnit.id().value(),
        orgUnit.code(),
        orgUnit.name(),
        orgUnit.parentId(),
        orgUnit.fullPath(),
        orgUnit.level(),
        orgUnit.managerEmployeeId(),
        orgUnit.sortOrder(),
        orgUnit.status(),
        orgUnit.createdAt(),
        leaderName);
  }

  /** 聚合转组织单元树形响应 */
  public OrgUnitTreeResponse toTreeResponse(
      OrgUnit orgUnit, String leaderName, List<OrgUnitTreeResponse> children) {
    return new OrgUnitTreeResponse(
        orgUnit.id().value(),
        orgUnit.code(),
        orgUnit.name(),
        orgUnit.parentId(),
        orgUnit.level(),
        orgUnit.managerEmployeeId(),
        orgUnit.sortOrder(),
        orgUnit.status(),
        leaderName,
        children);
  }
}

package io.github.lystrosaurus.admin.system.role.infrastructure.converter;

import io.github.lystrosaurus.admin.system.role.domain.model.Role;
import io.github.lystrosaurus.admin.system.role.domain.model.RoleId;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RolePO;
import org.springframework.stereotype.Component;

/** 角色聚合 ↔ 持久化对象转换器。审计字段由 MyBatis-Plus 自动填充，不在此转换。 */
@Component
public class RolePoConverter {

  /** 领域对象转持久化对象 */
  public RolePO toPo(Role role) {
    RolePO po = new RolePO();
    po.setId(role.id() == null ? null : role.id().value());
    po.setCode(role.code());
    po.setName(role.name());
    po.setDescription(role.description());
    po.setSortOrder(role.sortOrder());
    po.setStatus(role.status());
    po.setDataScopeType(role.dataScopeType());
    po.setVersion(role.version());
    return po;
  }

  /** 持久化对象转领域对象（重建聚合，不登记事件） */
  public Role toDomain(RolePO po) {
    return Role.restore(
        RoleId.of(po.getId()),
        po.getCode(),
        po.getName(),
        po.getDescription(),
        po.getSortOrder(),
        po.getStatus(),
        po.getDataScopeType(),
        po.getCreatedAt(),
        po.getVersion());
  }
}

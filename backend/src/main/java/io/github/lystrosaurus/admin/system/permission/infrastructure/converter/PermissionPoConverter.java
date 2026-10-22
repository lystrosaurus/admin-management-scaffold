package io.github.lystrosaurus.admin.system.permission.infrastructure.converter;

import io.github.lystrosaurus.admin.system.permission.domain.model.Permission;
import io.github.lystrosaurus.admin.system.permission.domain.model.PermissionId;
import io.github.lystrosaurus.admin.system.permission.infrastructure.persistence.po.PermissionPO;
import org.springframework.stereotype.Component;

/** 权限聚合 ↔ 持久化对象转换器。审计字段由 MyBatis-Plus 自动填充，不在此转换。 */
@Component
public class PermissionPoConverter {

  /** 领域对象转持久化对象 */
  public PermissionPO toPo(Permission permission) {
    PermissionPO po = new PermissionPO();
    po.setId(permission.id() == null ? null : permission.id().value());
    po.setCode(permission.code());
    po.setName(permission.name());
    po.setType(permission.type());
    po.setModule(permission.module());
    po.setResource(permission.resource());
    po.setAction(permission.action());
    po.setDescription(permission.description());
    po.setSortOrder(permission.sortOrder());
    po.setStatus(permission.status());
    po.setVersion(permission.version());
    return po;
  }

  /** 持久化对象转领域对象（重建聚合，不登记事件） */
  public Permission toDomain(PermissionPO po) {
    return Permission.restore(
        PermissionId.of(po.getId()),
        po.getCode(),
        po.getName(),
        po.getType(),
        po.getModule(),
        po.getResource(),
        po.getAction(),
        po.getDescription(),
        po.getSortOrder(),
        po.getStatus(),
        po.getCreatedAt(),
        po.getVersion());
  }
}

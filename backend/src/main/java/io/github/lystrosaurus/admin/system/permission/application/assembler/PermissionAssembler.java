package io.github.lystrosaurus.admin.system.permission.application.assembler;

import io.github.lystrosaurus.admin.system.permission.adapter.response.PermissionResponse;
import io.github.lystrosaurus.admin.system.permission.domain.model.Permission;
import org.springframework.stereotype.Component;

/** 权限装配器：聚合 → 响应模型。 */
@Component
public class PermissionAssembler {

  /** 聚合转权限响应 */
  public PermissionResponse toResponse(Permission permission) {
    return new PermissionResponse(
        permission.id().value(),
        permission.code(),
        permission.name(),
        permission.type(),
        permission.module(),
        permission.resource(),
        permission.action());
  }
}

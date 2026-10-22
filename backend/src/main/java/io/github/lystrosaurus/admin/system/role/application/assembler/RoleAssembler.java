package io.github.lystrosaurus.admin.system.role.application.assembler;

import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuSummary;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionSummary;
import io.github.lystrosaurus.admin.system.role.adapter.response.RoleDetailResponse;
import io.github.lystrosaurus.admin.system.role.adapter.response.RoleMenuResponse;
import io.github.lystrosaurus.admin.system.role.adapter.response.RolePermissionResponse;
import io.github.lystrosaurus.admin.system.role.adapter.response.RoleResponse;
import io.github.lystrosaurus.admin.system.role.domain.model.Role;
import java.util.List;
import org.springframework.stereotype.Component;

/** 角色装配器：聚合 → 响应模型。 */
@Component
public class RoleAssembler {

  /** 聚合转角色响应 */
  public RoleResponse toResponse(Role role) {
    return new RoleResponse(
        role.id().value(),
        role.code(),
        role.name(),
        role.description(),
        role.status(),
        role.dataScopeType());
  }

  /** 聚合与权限/菜单摘要转角色详情响应 */
  public RoleDetailResponse toDetailResponse(
      Role role, List<PermissionSummary> permissions, List<MenuSummary> menus) {
    List<RolePermissionResponse> permissionResponses =
        permissions.stream()
            .map(
                permission ->
                    new RolePermissionResponse(
                        permission.id(),
                        permission.code(),
                        permission.name(),
                        permission.type(),
                        permission.module(),
                        permission.resource(),
                        permission.action()))
            .toList();
    List<RoleMenuResponse> menuResponses =
        menus.stream()
            .map(
                menu ->
                    new RoleMenuResponse(
                        menu.id(),
                        menu.parentId(),
                        menu.name(),
                        menu.path(),
                        menu.component(),
                        menu.icon(),
                        menu.sortOrder(),
                        menu.type(),
                        menu.permissionCode(),
                        menu.visible(),
                        menu.status(),
                        null))
            .toList();
    return new RoleDetailResponse(
        role.id().value(),
        role.code(),
        role.name(),
        role.description(),
        role.sortOrder(),
        role.status(),
        role.dataScopeType(),
        permissionResponses,
        menuResponses,
        role.createdAt());
  }
}

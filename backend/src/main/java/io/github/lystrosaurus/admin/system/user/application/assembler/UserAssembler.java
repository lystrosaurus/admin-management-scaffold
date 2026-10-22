package io.github.lystrosaurus.admin.system.user.application.assembler;

import io.github.lystrosaurus.admin.system.user.adapter.response.UserDetailResponse;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserResponse;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserRoleResponse;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRoleInfo;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import java.util.List;
import org.springframework.stereotype.Component;

/** 用户装配器：聚合 → 响应模型。 */
@Component
public class UserAssembler {

  /** 聚合转用户响应 */
  public UserResponse toResponse(User user) {
    return new UserResponse(
        user.id().value(),
        user.username(),
        user.nickname(),
        user.phone() == null ? null : user.phone().value(),
        user.email() == null ? null : user.email().value(),
        user.status() == null ? null : user.status().name(),
        user.lastLoginAt(),
        user.createdAt());
  }

  /** 聚合与角色信息转用户详情响应 */
  public UserDetailResponse toDetailResponse(User user, List<UserRoleInfo> roles) {
    List<UserRoleResponse> roleResponses =
        roles.stream()
            .map(
                role ->
                    new UserRoleResponse(
                        role.id(),
                        role.code(),
                        role.name(),
                        role.description(),
                        role.status(),
                        role.dataScopeType()))
            .toList();
    return new UserDetailResponse(
        user.id().value(),
        user.username(),
        user.nickname(),
        user.phone() == null ? null : user.phone().value(),
        user.email() == null ? null : user.email().value(),
        user.status() == null ? null : user.status().name(),
        user.employeeId(),
        user.tokenVersion(),
        user.lastLoginAt(),
        user.lastLoginIp(),
        roleResponses,
        user.createdAt());
  }
}

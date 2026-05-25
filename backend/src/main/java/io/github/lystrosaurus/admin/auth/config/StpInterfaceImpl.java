package io.github.lystrosaurus.admin.auth.config;

import cn.dev33.satoken.stp.StpInterface;
import io.github.lystrosaurus.admin.system.permission.dao.PermissionDAO;
import io.github.lystrosaurus.admin.system.permission.entity.SysPermission;
import io.github.lystrosaurus.admin.system.role.dao.RoleDAO;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Sa-Token 权限数据源实现
 *
 * <p>从数据库加载用户的角色和权限列表，供 Sa-Token 鉴权使用。
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

  private final PermissionDAO permissionDAO;
  private final RoleDAO roleDAO;

  @Override
  public List<String> getPermissionList(Object loginId, String loginType) {
    Long userId = toUserId(loginId);
    if (userId == null) {
      return Collections.emptyList();
    }

    List<SysPermission> permissions = permissionDAO.findByUserId(userId);
    return permissions.stream()
        .map(SysPermission::getCode)
        .filter(code -> code != null && !code.isBlank())
        .toList();
  }

  @Override
  public List<String> getRoleList(Object loginId, String loginType) {
    Long userId = toUserId(loginId);
    if (userId == null) {
      return Collections.emptyList();
    }

    List<SysRole> roles = roleDAO.findByUserId(userId);
    return roles.stream()
        .map(SysRole::getCode)
        .filter(code -> code != null && !code.isBlank())
        .toList();
  }

  private Long toUserId(Object loginId) {
    if (loginId == null) {
      return null;
    }
    if (loginId instanceof Long) {
      return (Long) loginId;
    }
    if (loginId instanceof Integer) {
      return ((Integer) loginId).longValue();
    }
    try {
      return Long.parseLong(loginId.toString());
    } catch (NumberFormatException e) {
      return null;
    }
  }
}

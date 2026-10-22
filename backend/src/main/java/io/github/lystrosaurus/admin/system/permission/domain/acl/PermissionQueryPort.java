package io.github.lystrosaurus.admin.system.permission.domain.acl;

import java.util.List;

/** 权限查询端口：供 auth 鉴权与 role 详情读取权限。 */
public interface PermissionQueryPort {

  /** 查询用户拥有的权限码列表 */
  List<String> findCodesByUserId(Long userId);

  /** 查询角色拥有的权限摘要列表 */
  List<PermissionSummary> findByRoleId(Long roleId);
}

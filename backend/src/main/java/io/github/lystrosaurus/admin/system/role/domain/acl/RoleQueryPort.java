package io.github.lystrosaurus.admin.system.role.domain.acl;

import java.util.List;

/** 角色查询端口：供 auth、数据权限等其它上下文读取用户角色。 */
public interface RoleQueryPort {

  /** 查询用户拥有的角色摘要列表 */
  List<RoleSummary> findByUserId(Long userId);
}

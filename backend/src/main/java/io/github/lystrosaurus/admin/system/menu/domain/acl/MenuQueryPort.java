package io.github.lystrosaurus.admin.system.menu.domain.acl;

import java.util.List;

/** 菜单查询端口：供 auth/profile 与 role 详情读取菜单。 */
public interface MenuQueryPort {

  /** 查询用户拥有的菜单摘要列表 */
  List<MenuSummary> findByUserId(Long userId);

  /** 查询角色拥有的菜单摘要列表 */
  List<MenuSummary> findByRoleId(Long roleId);
}

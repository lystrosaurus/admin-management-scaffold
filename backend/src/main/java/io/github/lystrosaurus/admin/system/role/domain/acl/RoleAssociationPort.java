package io.github.lystrosaurus.admin.system.role.domain.acl;

import java.util.List;

/**
 * 角色关联端口：角色与用户/权限/菜单/组织关联的读写契约。
 *
 * <p>供本上下文及其它上下文（auth、organization 数据权限、permission/menu 按角色/用户反查）使用。
 */
public interface RoleAssociationPort {

  /** 查询用户拥有的角色ID列表 */
  List<Long> findRoleIdsByUserId(Long userId);

  /** 查询角色拥有的权限ID列表 */
  List<Long> findPermissionIdsByRoleId(Long roleId);

  /** 查询用户拥有的权限ID列表（经用户角色 → 角色权限两级关联） */
  List<Long> findPermissionIdsByUserId(Long userId);

  /** 查询角色拥有的菜单ID列表 */
  List<Long> findMenuIdsByRoleId(Long roleId);

  /** 查询用户拥有的菜单ID列表（经用户角色 → 角色菜单两级关联） */
  List<Long> findMenuIdsByUserId(Long userId);

  /** 查询角色绑定的组织ID列表（数据权限 CUSTOM/ORG_TREE/ORG_ONLY 范围） */
  List<Long> findOrgIdsByRoleId(Long roleId);

  /** 全量替换用户角色关联（先清空再批量插入） */
  void assignRoles(Long userId, List<Long> roleIds);

  /** 移除用户的所有角色关联 */
  void removeRoles(Long userId);
}

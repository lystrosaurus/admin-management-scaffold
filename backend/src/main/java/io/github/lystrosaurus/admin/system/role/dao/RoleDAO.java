package io.github.lystrosaurus.admin.system.role.dao;

import io.github.lystrosaurus.admin.system.menu.entity.SysMenu;
import io.github.lystrosaurus.admin.system.permission.entity.SysPermission;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import java.util.List;

/**
 * 角色数据访问对象接口
 *
 * <p>定义 Service 需要的数据访问语义，而不是简单地暴露 Mapper 方法。
 */
public interface RoleDAO {

  /**
   * 根据ID查找角色
   *
   * @param id 角色ID
   * @return 角色实体，不存在时返回null
   */
  SysRole findById(Long id);

  /**
   * 根据角色编码查找角色
   *
   * @param code 角色编码
   * @return 角色实体，不存在时返回null
   */
  SysRole findByCode(String code);

  /**
   * 保存角色
   *
   * @param role 角色实体
   */
  void save(SysRole role);

  /**
   * 更新角色
   *
   * @param role 角色实体
   */
  void update(SysRole role);

  /**
   * 根据ID删除角色
   *
   * @param id 角色ID
   */
  void deleteById(Long id);

  /**
   * 根据条件查找角色列表
   *
   * @param keyword 关键词（可选）
   * @param status 状态（可选）
   * @param page 页码
   * @param size 每页大小
   * @return 角色列表
   */
  List<SysRole> findByCondition(String keyword, String status, int page, int size);

  /**
   * 根据条件统计角色数量
   *
   * @param keyword 关键词（可选）
   * @param status 状态（可选）
   * @return 角色数量
   */
  long countByCondition(String keyword, String status);

  /**
   * 检查角色编码是否存在
   *
   * @param code 角色编码
   * @return 存在返回true，否则返回false
   */
  boolean existsByCode(String code);

  /**
   * 检查角色编码是否存在且排除指定ID
   *
   * @param code 角色编码
   * @param id 排除的角色ID
   * @return 存在返回true，否则返回false
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * 查找角色关联的权限
   *
   * @param roleId 角色ID
   * @return 权限列表
   */
  List<SysPermission> findPermissionsByRoleId(Long roleId);

  /**
   * 分配权限给角色
   *
   * @param roleId 角色ID
   * @param permissionIds 权限ID列表
   */
  void assignPermissions(Long roleId, List<Long> permissionIds);

  /**
   * 移除角色的所有权限
   *
   * @param roleId 角色ID
   */
  void removePermissions(Long roleId);

  /**
   * 查找角色关联的菜单
   *
   * @param roleId 角色ID
   * @return 菜单列表
   */
  List<SysMenu> findMenusByRoleId(Long roleId);

  /**
   * 分配菜单给角色
   *
   * @param roleId 角色ID
   * @param menuIds 菜单ID列表
   */
  void assignMenus(Long roleId, List<Long> menuIds);

  /**
   * 移除角色的所有菜单
   *
   * @param roleId 角色ID
   */
  void removeMenus(Long roleId);
}

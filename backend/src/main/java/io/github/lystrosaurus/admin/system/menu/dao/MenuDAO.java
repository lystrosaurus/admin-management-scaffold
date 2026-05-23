package io.github.lystrosaurus.admin.system.menu.dao;

import io.github.lystrosaurus.admin.system.menu.entity.SysMenu;
import java.util.List;

/**
 * 菜单数据访问对象接口
 *
 * <p>定义 Service 需要的数据访问语义，而不是简单地暴露 Mapper 方法。
 */
public interface MenuDAO {

  /**
   * 根据ID查找菜单
   *
   * @param id 菜单ID
   * @return 菜单实体，不存在时返回null
   */
  SysMenu findById(Long id);

  /**
   * 保存菜单
   *
   * @param menu 菜单实体
   */
  void save(SysMenu menu);

  /**
   * 更新菜单
   *
   * @param menu 菜单实体
   */
  void update(SysMenu menu);

  /**
   * 根据ID删除菜单
   *
   * @param id 菜单ID
   */
  void deleteById(Long id);

  /**
   * 查找所有菜单
   *
   * @return 菜单列表
   */
  List<SysMenu> findAll();

  /**
   * 根据父菜单ID查找子菜单
   *
   * @param parentId 父菜单ID
   * @return 子菜单列表
   */
  List<SysMenu> findByParentId(Long parentId);

  /**
   * 根据角色ID查找菜单
   *
   * @param roleId 角色ID
   * @return 菜单列表
   */
  List<SysMenu> findByRoleId(Long roleId);

  /**
   * 根据用户ID查找菜单
   *
   * @param userId 用户ID
   * @return 菜单列表
   */
  List<SysMenu> findByUserId(Long userId);

  /**
   * 检查权限编码是否存在
   *
   * @param permissionCode 权限编码
   * @return 存在返回true，否则返回false
   */
  boolean existsByPermissionCode(String permissionCode);

  /**
   * 更新菜单状态
   *
   * @param id 菜单ID
   * @param status 状态值
   */
  void updateStatus(Long id, Integer status);
}

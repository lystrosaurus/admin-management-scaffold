package io.github.lystrosaurus.admin.system.menu.service;

import io.github.lystrosaurus.admin.system.menu.dto.MenuCreateDTO;
import io.github.lystrosaurus.admin.system.menu.dto.MenuUpdateDTO;
import io.github.lystrosaurus.admin.system.menu.vo.MenuVO;
import java.util.List;

/** 菜单服务接口 */
public interface MenuService {

  /**
   * 创建菜单
   *
   * @param dto 菜单创建DTO
   * @return 菜单VO
   */
  MenuVO create(MenuCreateDTO dto);

  /**
   * 更新菜单
   *
   * @param id 菜单ID
   * @param dto 菜单更新DTO
   * @return 菜单VO
   */
  MenuVO update(Long id, MenuUpdateDTO dto);

  /**
   * 删除菜单
   *
   * @param id 菜单ID
   */
  void deleteById(Long id);

  /**
   * 获取菜单树
   *
   * @return 菜单树列表
   */
  List<MenuVO> findTree();

  /**
   * 根据角色ID查询菜单
   *
   * @param roleId 角色ID
   * @return 菜单列表
   */
  List<MenuVO> findByRoleId(Long roleId);

  /**
   * 根据用户ID查询菜单
   *
   * @param userId 用户ID
   * @return 菜单列表
   */
  List<MenuVO> findByUserId(Long userId);
}

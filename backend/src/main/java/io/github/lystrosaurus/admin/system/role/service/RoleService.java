package io.github.lystrosaurus.admin.system.role.service;

import io.github.lystrosaurus.admin.system.role.dto.RoleCreateDTO;
import io.github.lystrosaurus.admin.system.role.dto.RoleUpdateDTO;
import io.github.lystrosaurus.admin.system.role.vo.RoleDetailVO;
import io.github.lystrosaurus.admin.system.role.vo.RoleVO;
import java.util.List;

/** 角色服务接口 */
public interface RoleService {

  /**
   * 创建角色
   *
   * @param dto 角色创建DTO
   * @return 角色VO
   */
  RoleVO create(RoleCreateDTO dto);

  /**
   * 更新角色
   *
   * @param id 角色ID
   * @param dto 角色更新DTO
   * @return 角色VO
   */
  RoleVO update(Long id, RoleUpdateDTO dto);

  /**
   * 删除角色
   *
   * @param id 角色ID
   */
  void deleteById(Long id);

  /**
   * 查询角色详情
   *
   * @param id 角色ID
   * @return 角色详情VO
   */
  RoleDetailVO findById(Long id);

  /**
   * 查询所有角色
   *
   * @return 角色列表
   */
  List<RoleVO> findAll();

  /**
   * 根据用户ID查询角色
   *
   * @param userId 用户ID
   * @return 角色列表
   */
  List<RoleVO> findByUserId(Long userId);

  /**
   * 分配权限给角色
   *
   * @param roleId 角色ID
   * @param permissionIds 权限ID列表
   */
  void assignPermissions(Long roleId, List<Long> permissionIds);

  /**
   * 分配菜单给角色
   *
   * @param roleId 角色ID
   * @param menuIds 菜单ID列表
   */
  void assignMenus(Long roleId, List<Long> menuIds);
}

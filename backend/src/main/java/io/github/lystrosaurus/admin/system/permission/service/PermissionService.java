package io.github.lystrosaurus.admin.system.permission.service;

import io.github.lystrosaurus.admin.system.permission.dto.PermissionCreateDTO;
import io.github.lystrosaurus.admin.system.permission.dto.PermissionUpdateDTO;
import io.github.lystrosaurus.admin.system.permission.vo.PermissionVO;
import java.util.List;

/** 权限服务接口 */
public interface PermissionService {

  /**
   * 创建权限
   *
   * @param dto 权限创建DTO
   * @return 权限VO
   */
  PermissionVO create(PermissionCreateDTO dto);

  /**
   * 更新权限
   *
   * @param id 权限ID
   * @param dto 权限更新DTO
   * @return 权限VO
   */
  PermissionVO update(Long id, PermissionUpdateDTO dto);

  /**
   * 删除权限
   *
   * @param id 权限ID
   */
  void deleteById(Long id);

  /**
   * 根据角色ID查询权限
   *
   * @param roleId 角色ID
   * @return 权限列表
   */
  List<PermissionVO> findByRoleId(Long roleId);

  /**
   * 根据用户ID查询权限
   *
   * @param userId 用户ID
   * @return 权限列表
   */
  List<PermissionVO> findByUserId(Long userId);

  /**
   * 查询所有权限
   *
   * @return 全部权限列表
   */
  List<PermissionVO> findAll();
}

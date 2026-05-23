package io.github.lystrosaurus.admin.system.role.controller;

import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.system.role.dto.RoleCreateDTO;
import io.github.lystrosaurus.admin.system.role.dto.RoleUpdateDTO;
import io.github.lystrosaurus.admin.system.role.service.RoleService;
import io.github.lystrosaurus.admin.system.role.vo.RoleDetailVO;
import io.github.lystrosaurus.admin.system.role.vo.RoleVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色控制器
 *
 * <p>提供角色管理相关接口
 */
@RestController
@RequestMapping("/app/roles")
@RequiredArgsConstructor
public class RoleController {

  private final RoleService roleService;

  /**
   * 创建角色
   *
   * @param dto 角色创建DTO
   * @return 角色VO
   */
  @PostMapping
  public ApiResponse<RoleVO> create(@RequestBody @Valid RoleCreateDTO dto) {
    RoleVO roleVO = roleService.create(dto);
    return ApiResponse.success(roleVO);
  }

  /**
   * 获取角色详情
   *
   * @param id 角色ID
   * @return 角色详情VO
   */
  @GetMapping("/{id}")
  public ApiResponse<RoleDetailVO> findById(@PathVariable Long id) {
    RoleDetailVO roleDetailVO = roleService.findById(id);
    return ApiResponse.success(roleDetailVO);
  }

  /**
   * 更新角色
   *
   * @param id 角色ID
   * @param dto 角色更新DTO
   * @return 角色VO
   */
  @PutMapping("/{id}")
  public ApiResponse<RoleVO> update(@PathVariable Long id, @RequestBody RoleUpdateDTO dto) {
    RoleVO roleVO = roleService.update(id, dto);
    return ApiResponse.success(roleVO);
  }

  /**
   * 删除角色
   *
   * @param id 角色ID
   * @return 空响应
   */
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    roleService.deleteById(id);
    return ApiResponse.success();
  }

  /**
   * 查询所有角色
   *
   * @return 角色列表
   */
  @GetMapping
  public ApiResponse<List<RoleVO>> findAll() {
    List<RoleVO> roles = roleService.findAll();
    return ApiResponse.success(roles);
  }

  /**
   * 分配权限给角色
   *
   * @param roleId 角色ID
   * @param permissionIds 权限ID列表
   * @return 空响应
   */
  @PostMapping("/{id}/permissions")
  public ApiResponse<Void> assignPermissions(
      @PathVariable("id") Long roleId, @RequestBody List<Long> permissionIds) {
    roleService.assignPermissions(roleId, permissionIds);
    return ApiResponse.success();
  }

  /**
   * 分配菜单给角色
   *
   * @param roleId 角色ID
   * @param menuIds 菜单ID列表
   * @return 空响应
   */
  @PostMapping("/{id}/menus")
  public ApiResponse<Void> assignMenus(
      @PathVariable("id") Long roleId, @RequestBody List<Long> menuIds) {
    roleService.assignMenus(roleId, menuIds);
    return ApiResponse.success();
  }
}

package io.github.lystrosaurus.admin.system.role.adapter.web;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
import io.github.lystrosaurus.admin.system.role.adapter.request.RoleCreateRequest;
import io.github.lystrosaurus.admin.system.role.adapter.request.RoleUpdateRequest;
import io.github.lystrosaurus.admin.system.role.adapter.response.RoleDetailResponse;
import io.github.lystrosaurus.admin.system.role.adapter.response.RoleResponse;
import io.github.lystrosaurus.admin.system.role.application.RoleApplicationService;
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

  private final RoleApplicationService roleApplicationService;

  /**
   * 创建角色
   *
   * @param request 角色创建请求
   * @return 角色响应
   */
  @RequiresPermission("system:role:create")
  @PostMapping
  public ApiResponse<RoleResponse> create(@RequestBody @Valid RoleCreateRequest request) {
    RoleResponse response = roleApplicationService.create(request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 获取角色详情
   *
   * @param id 角色ID
   * @return 角色详情响应
   */
  @RequiresPermission("system:role:view")
  @GetMapping("/{id}")
  public ApiResponse<RoleDetailResponse> findById(@PathVariable Long id) {
    RoleDetailResponse response = roleApplicationService.findById(id);
    return ApiResponse.success(response);
  }

  /**
   * 更新角色
   *
   * @param id 角色ID
   * @param request 角色更新请求
   * @return 角色响应
   */
  @RequiresPermission("system:role:update")
  @PutMapping("/{id}")
  public ApiResponse<RoleResponse> update(
      @PathVariable Long id, @RequestBody RoleUpdateRequest request) {
    RoleResponse response = roleApplicationService.update(id, request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 删除角色
   *
   * @param id 角色ID
   * @return 空响应
   */
  @RequiresPermission("system:role:delete")
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    roleApplicationService.deleteById(id);
    return ApiResponse.success();
  }

  /**
   * 查询所有角色
   *
   * @return 角色列表
   */
  @RequiresPermission("system:role:view")
  @GetMapping
  public ApiResponse<List<RoleResponse>> findAll() {
    List<RoleResponse> roles = roleApplicationService.findAll();
    return ApiResponse.success(roles);
  }

  /**
   * 分配权限给角色
   *
   * @param roleId 角色ID
   * @param permissionIds 权限ID列表
   * @return 空响应
   */
  @RequiresPermission("system:role:assign-permission")
  @PostMapping("/{id}/permissions")
  public ApiResponse<Void> assignPermissions(
      @PathVariable("id") Long roleId, @RequestBody List<Long> permissionIds) {
    roleApplicationService.assignPermissions(roleId, permissionIds);
    return ApiResponse.success();
  }

  /**
   * 分配菜单给角色
   *
   * @param roleId 角色ID
   * @param menuIds 菜单ID列表
   * @return 空响应
   */
  @RequiresPermission("system:role:assign-menu")
  @PostMapping("/{id}/menus")
  public ApiResponse<Void> assignMenus(
      @PathVariable("id") Long roleId, @RequestBody List<Long> menuIds) {
    roleApplicationService.assignMenus(roleId, menuIds);
    return ApiResponse.success();
  }
}

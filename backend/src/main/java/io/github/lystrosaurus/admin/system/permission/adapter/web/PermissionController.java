package io.github.lystrosaurus.admin.system.permission.adapter.web;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.system.permission.adapter.request.PermissionCreateRequest;
import io.github.lystrosaurus.admin.system.permission.adapter.request.PermissionUpdateRequest;
import io.github.lystrosaurus.admin.system.permission.adapter.response.PermissionResponse;
import io.github.lystrosaurus.admin.system.permission.application.PermissionApplicationService;
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
 * 权限控制器
 *
 * <p>提供权限管理相关接口
 */
@RestController
@RequestMapping("/app/permissions")
@RequiredArgsConstructor
public class PermissionController {

  private final PermissionApplicationService permissionApplicationService;

  /**
   * 查询所有权限
   *
   * @return 权限列表
   */
  @RequiresPermission("system:permission:view")
  @GetMapping
  public ApiResponse<List<PermissionResponse>> findAll() {
    List<PermissionResponse> permissions = permissionApplicationService.findAll();
    return ApiResponse.success(permissions);
  }

  /**
   * 创建权限
   *
   * @param request 权限创建请求
   * @return 权限响应
   */
  @RequiresPermission("system:permission:create")
  @PostMapping
  public ApiResponse<PermissionResponse> create(
      @RequestBody @Valid PermissionCreateRequest request) {
    PermissionResponse response = permissionApplicationService.create(request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 更新权限
   *
   * @param id 权限ID
   * @param request 权限更新请求
   * @return 权限响应
   */
  @RequiresPermission("system:permission:update")
  @PutMapping("/{id}")
  public ApiResponse<PermissionResponse> update(
      @PathVariable Long id, @RequestBody PermissionUpdateRequest request) {
    PermissionResponse response = permissionApplicationService.update(id, request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 删除权限
   *
   * @param id 权限ID
   * @return 空响应
   */
  @RequiresPermission("system:permission:delete")
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    permissionApplicationService.deleteById(id);
    return ApiResponse.success();
  }

  /**
   * 按角色查询权限
   *
   * @param roleId 角色ID
   * @return 权限列表
   */
  @RequiresPermission("system:permission:view")
  @GetMapping("/role/{roleId}")
  public ApiResponse<List<PermissionResponse>> findByRoleId(@PathVariable Long roleId) {
    List<PermissionResponse> permissions = permissionApplicationService.findByRoleId(roleId);
    return ApiResponse.success(permissions);
  }
}

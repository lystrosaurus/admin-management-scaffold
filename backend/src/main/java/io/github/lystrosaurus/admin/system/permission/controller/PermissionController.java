package io.github.lystrosaurus.admin.system.permission.controller;

import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.system.permission.dto.PermissionCreateDTO;
import io.github.lystrosaurus.admin.system.permission.dto.PermissionUpdateDTO;
import io.github.lystrosaurus.admin.system.permission.service.PermissionService;
import io.github.lystrosaurus.admin.system.permission.vo.PermissionVO;
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

  private final PermissionService permissionService;

  /**
   * 创建权限
   *
   * @param dto 权限创建DTO
   * @return 权限VO
   */
  @PostMapping
  public ApiResponse<PermissionVO> create(@RequestBody @Valid PermissionCreateDTO dto) {
    PermissionVO permissionVO = permissionService.create(dto);
    return ApiResponse.success(permissionVO);
  }

  /**
   * 更新权限
   *
   * @param id 权限ID
   * @param dto 权限更新DTO
   * @return 权限VO
   */
  @PutMapping("/{id}")
  public ApiResponse<PermissionVO> update(
      @PathVariable Long id, @RequestBody PermissionUpdateDTO dto) {
    PermissionVO permissionVO = permissionService.update(id, dto);
    return ApiResponse.success(permissionVO);
  }

  /**
   * 删除权限
   *
   * @param id 权限ID
   * @return 空响应
   */
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    permissionService.deleteById(id);
    return ApiResponse.success();
  }

  /**
   * 按角色查询权限
   *
   * @param roleId 角色ID
   * @return 权限列表
   */
  @GetMapping("/role/{roleId}")
  public ApiResponse<List<PermissionVO>> findByRoleId(@PathVariable Long roleId) {
    List<PermissionVO> permissions = permissionService.findByRoleId(roleId);
    return ApiResponse.success(permissions);
  }
}

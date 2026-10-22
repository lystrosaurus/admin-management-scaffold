package io.github.lystrosaurus.admin.system.menu.adapter.web;

import io.github.lystrosaurus.admin.auth.annotation.PermitAll;
import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.auth.context.UserContext;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
import io.github.lystrosaurus.admin.system.menu.adapter.request.MenuCreateRequest;
import io.github.lystrosaurus.admin.system.menu.adapter.request.MenuUpdateRequest;
import io.github.lystrosaurus.admin.system.menu.adapter.response.MenuResponse;
import io.github.lystrosaurus.admin.system.menu.application.MenuApplicationService;
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
 * 菜单控制器
 *
 * <p>提供菜单管理相关接口
 */
@RestController
@RequestMapping("/app/menus")
@RequiredArgsConstructor
public class MenuController {

  private final MenuApplicationService menuApplicationService;

  /**
   * 创建菜单
   *
   * @param request 菜单创建请求
   * @return 菜单响应
   */
  @RequiresPermission("system:menu:create")
  @PostMapping
  public ApiResponse<MenuResponse> create(@RequestBody @Valid MenuCreateRequest request) {
    MenuResponse response = menuApplicationService.create(request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 更新菜单
   *
   * @param id 菜单ID
   * @param request 菜单更新请求
   * @return 菜单响应
   */
  @RequiresPermission("system:menu:update")
  @PutMapping("/{id}")
  public ApiResponse<MenuResponse> update(
      @PathVariable Long id, @RequestBody MenuUpdateRequest request) {
    MenuResponse response = menuApplicationService.update(id, request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 删除菜单
   *
   * @param id 菜单ID
   * @return 空响应
   */
  @RequiresPermission("system:menu:delete")
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    menuApplicationService.deleteById(id);
    return ApiResponse.success();
  }

  /**
   * 获取菜单树
   *
   * @return 菜单树列表
   */
  @RequiresPermission("system:menu:view")
  @GetMapping("/tree")
  public ApiResponse<List<MenuResponse>> findTree() {
    List<MenuResponse> menuTree = menuApplicationService.findTree();
    return ApiResponse.success(menuTree);
  }

  /**
   * 按角色查询菜单
   *
   * @param roleId 角色ID
   * @return 菜单列表
   */
  @RequiresPermission("system:menu:view")
  @GetMapping("/role/{roleId}")
  public ApiResponse<List<MenuResponse>> findByRoleId(@PathVariable Long roleId) {
    List<MenuResponse> menus = menuApplicationService.findByRoleId(roleId);
    return ApiResponse.success(menus);
  }

  /**
   * 获取当前用户的菜单树
   *
   * @return 菜单树列表
   */
  @PermitAll(reason = "当前登录用户的菜单树，只读自身")
  @GetMapping("/user")
  public ApiResponse<List<MenuResponse>> findUserMenuTree() {
    Long currentUserId = UserContext.getCurrentUserId();
    List<MenuResponse> userMenus = menuApplicationService.findByUserId(currentUserId);
    return ApiResponse.success(userMenus);
  }
}

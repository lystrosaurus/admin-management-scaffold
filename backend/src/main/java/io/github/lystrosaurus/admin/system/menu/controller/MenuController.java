package io.github.lystrosaurus.admin.system.menu.controller;

import io.github.lystrosaurus.admin.auth.context.UserContext;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.system.menu.dto.MenuCreateDTO;
import io.github.lystrosaurus.admin.system.menu.dto.MenuUpdateDTO;
import io.github.lystrosaurus.admin.system.menu.service.MenuService;
import io.github.lystrosaurus.admin.system.menu.vo.MenuVO;
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

  private final MenuService menuService;

  /**
   * 创建菜单
   *
   * @param dto 菜单创建DTO
   * @return 菜单VO
   */
  @PostMapping
  public ApiResponse<MenuVO> create(@RequestBody @Valid MenuCreateDTO dto) {
    MenuVO menuVO = menuService.create(dto);
    return ApiResponse.success(menuVO);
  }

  /**
   * 更新菜单
   *
   * @param id 菜单ID
   * @param dto 菜单更新DTO
   * @return 菜单VO
   */
  @PutMapping("/{id}")
  public ApiResponse<MenuVO> update(@PathVariable Long id, @RequestBody MenuUpdateDTO dto) {
    MenuVO menuVO = menuService.update(id, dto);
    return ApiResponse.success(menuVO);
  }

  /**
   * 删除菜单
   *
   * @param id 菜单ID
   * @return 空响应
   */
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    menuService.deleteById(id);
    return ApiResponse.success();
  }

  /**
   * 获取菜单树
   *
   * @return 菜单树列表
   */
  @GetMapping("/tree")
  public ApiResponse<List<MenuVO>> findTree() {
    List<MenuVO> menuTree = menuService.findTree();
    return ApiResponse.success(menuTree);
  }

  /**
   * 按角色查询菜单
   *
   * @param roleId 角色ID
   * @return 菜单列表
   */
  @GetMapping("/role/{roleId}")
  public ApiResponse<List<MenuVO>> findByRoleId(@PathVariable Long roleId) {
    List<MenuVO> menus = menuService.findByRoleId(roleId);
    return ApiResponse.success(menus);
  }

  /**
   * 获取当前用户的菜单树
   *
   * @return 菜单树列表
   */
  @GetMapping("/user")
  public ApiResponse<List<MenuVO>> findUserMenuTree() {
    Long currentUserId = UserContext.getCurrentUserId();
    List<MenuVO> userMenus = menuService.findByUserId(currentUserId);
    return ApiResponse.success(userMenus);
  }
}

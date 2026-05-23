package io.github.lystrosaurus.admin.auth.controller;

import io.github.lystrosaurus.admin.auth.context.UserContext;
import io.github.lystrosaurus.admin.auth.vo.ProfileVO;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.system.menu.service.MenuService;
import io.github.lystrosaurus.admin.system.menu.vo.MenuVO;
import io.github.lystrosaurus.admin.system.role.service.RoleService;
import io.github.lystrosaurus.admin.system.role.vo.RoleVO;
import io.github.lystrosaurus.admin.system.user.dto.ChangePasswordDTO;
import io.github.lystrosaurus.admin.system.user.service.UserService;
import io.github.lystrosaurus.admin.system.user.vo.UserDetailVO;
import io.github.lystrosaurus.admin.system.user.vo.UserVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户资料控制器
 *
 * <p>提供当前登录用户资料相关接口
 */
@RestController
@RequestMapping("/app/profile")
@RequiredArgsConstructor
public class ProfileController {

  private final UserService userService;
  private final RoleService roleService;
  private final MenuService menuService;

  /**
   * 获取当前用户资料
   *
   * @return 用户资料VO
   */
  @GetMapping
  public ApiResponse<ProfileVO> getProfile() {
    Long currentUserId = UserContext.getCurrentUserId();

    // 查询用户信息
    UserDetailVO userDetail = userService.findById(currentUserId);
    UserVO userVO =
        new UserVO(
            userDetail.id(),
            userDetail.username(),
            userDetail.nickname(),
            userDetail.phone(),
            userDetail.email(),
            userDetail.status(),
            userDetail.lastLoginAt(),
            userDetail.createdAt());

    // 查询用户角色
    List<RoleVO> roles = roleService.findByUserId(currentUserId);
    List<String> roleNames = roles.stream().map(RoleVO::code).toList();

    // 查询用户权限
    List<String> permissions = UserContext.getPermissions();

    // 查询用户菜单
    List<MenuVO> menus = menuService.findByUserId(currentUserId);

    ProfileVO profileVO = new ProfileVO(userVO, roleNames, permissions, menus);
    return ApiResponse.success(profileVO);
  }

  /**
   * 修改自己的密码
   *
   * @param dto 修改密码DTO
   * @return 空响应
   */
  @PutMapping("/password")
  public ApiResponse<Void> changePassword(@RequestBody @Valid ChangePasswordDTO dto) {
    Long currentUserId = UserContext.getCurrentUserId();
    userService.changePassword(currentUserId, dto);
    return ApiResponse.success();
  }

  /**
   * 获取当前用户权限码列表
   *
   * @return 权限码列表
   */
  @GetMapping("/permissions")
  public ApiResponse<List<String>> getPermissions() {
    List<String> permissions = UserContext.getPermissions();
    return ApiResponse.success(permissions);
  }
}

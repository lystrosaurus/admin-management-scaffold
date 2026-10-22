package io.github.lystrosaurus.admin.auth.controller;

import io.github.lystrosaurus.admin.auth.annotation.PermitAll;
import io.github.lystrosaurus.admin.auth.context.UserContext;
import io.github.lystrosaurus.admin.auth.vo.ProfileVO;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.system.menu.adapter.response.MenuResponse;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuQueryPort;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuSummary;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleQueryPort;
import io.github.lystrosaurus.admin.system.user.adapter.request.ChangePasswordRequest;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserDetailResponse;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserResponse;
import io.github.lystrosaurus.admin.system.user.application.UserApplicationService;
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

  private final UserApplicationService userApplicationService;
  private final RoleQueryPort roleQueryPort;
  private final MenuQueryPort menuQueryPort;

  /**
   * 获取当前用户资料
   *
   * @return 用户资料VO
   */
  @PermitAll(reason = "当前登录用户的资料，只读自身")
  @GetMapping
  public ApiResponse<ProfileVO> getProfile() {
    Long currentUserId = UserContext.getCurrentUserId();

    // 查询用户信息
    UserDetailResponse userDetail = userApplicationService.findById(currentUserId);
    UserResponse userResponse =
        new UserResponse(
            userDetail.id(),
            userDetail.username(),
            userDetail.nickname(),
            userDetail.phone(),
            userDetail.email(),
            userDetail.status(),
            userDetail.lastLoginAt(),
            userDetail.createdAt());

    // 查询用户角色
    List<String> roleNames = roleQueryPort.findByUserId(currentUserId).stream()
        .map(role -> role.code())
        .toList();

    // 查询用户权限
    List<String> permissions = UserContext.getPermissions();

    // 查询用户菜单
    List<MenuResponse> menus =
        menuQueryPort.findByUserId(currentUserId).stream().map(this::toMenuResponse).toList();

    ProfileVO profileVO = new ProfileVO(userResponse, roleNames, permissions, menus);
    return ApiResponse.success(profileVO);
  }

  /**
   * 修改自己的密码
   *
   * @param request 修改密码请求
   * @return 空响应
   */
  @PermitAll(reason = "修改当前登录用户自身密码")
  @PutMapping("/password")
  public ApiResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
    Long currentUserId = UserContext.getCurrentUserId();
    userApplicationService.changePassword(currentUserId, request.toCommand());
    return ApiResponse.success();
  }

  /**
   * 获取当前用户权限码列表
   *
   * @return 权限码列表
   */
  @PermitAll(reason = "查看当前登录用户自身权限码")
  @GetMapping("/permissions")
  public ApiResponse<List<String>> getPermissions() {
    List<String> permissions = UserContext.getPermissions();
    return ApiResponse.success(permissions);
  }

  /** 菜单摘要转资料菜单响应（不构建子树，保持与原 MenuVO 一致的扁平结构） */
  private MenuResponse toMenuResponse(MenuSummary menu) {
    return new MenuResponse(
        menu.id(),
        menu.parentId(),
        menu.name(),
        menu.path(),
        menu.component(),
        menu.icon(),
        menu.sortOrder(),
        menu.type(),
        menu.permissionCode(),
        menu.visible(),
        menu.status(),
        null);
  }
}

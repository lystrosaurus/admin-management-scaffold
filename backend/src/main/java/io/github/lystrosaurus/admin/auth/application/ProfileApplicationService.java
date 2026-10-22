package io.github.lystrosaurus.admin.auth.application;

import io.github.lystrosaurus.admin.auth.adapter.response.ExternalAccountResponse;
import io.github.lystrosaurus.admin.auth.adapter.response.ProfileResponse;
import io.github.lystrosaurus.admin.auth.adapter.response.SecurityProfileResponse;
import io.github.lystrosaurus.admin.auth.domain.acl.TokenSessionPort;
import io.github.lystrosaurus.admin.auth.log.service.LoginLogService;
import io.github.lystrosaurus.admin.auth.log.vo.LoginLogVO;
import io.github.lystrosaurus.admin.system.menu.adapter.response.MenuResponse;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuQueryPort;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuSummary;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleQueryPort;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserDetailResponse;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserResponse;
import io.github.lystrosaurus.admin.system.user.application.UserApplicationService;
import io.github.lystrosaurus.admin.system.user.application.command.ChangePasswordCommand;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 资料应用服务：组装当前用户的资料与账号安全信息读模型。 */
@Service
@RequiredArgsConstructor
public class ProfileApplicationService {

  private final UserApplicationService userApplicationService;
  private final RoleQueryPort roleQueryPort;
  private final MenuQueryPort menuQueryPort;
  private final ExternalAccountApplicationService externalAccountApplicationService;
  private final LoginLogService loginLogService;
  private final AuthApplicationService authApplicationService;
  private final TokenSessionPort tokenSessionPort;

  /** 获取当前用户资料 */
  public ProfileResponse getCurrentProfile() {
    Long currentUserId = tokenSessionPort.currentUserId();

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
    List<String> roleNames =
        roleQueryPort.findByUserId(currentUserId).stream().map(role -> role.code()).toList();

    // 查询用户权限
    List<String> permissions = tokenSessionPort.currentPermissions();

    // 查询用户菜单
    List<MenuResponse> menus =
        menuQueryPort.findByUserId(currentUserId).stream().map(this::toMenuResponse).toList();

    return new ProfileResponse(userResponse, roleNames, permissions, menus);
  }

  /** 获取当前用户权限码列表 */
  public List<String> getCurrentPermissions() {
    return tokenSessionPort.currentPermissions();
  }

  /** 修改当前用户密码 */
  public void changePassword(ChangePasswordCommand command) {
    userApplicationService.changePassword(tokenSessionPort.currentUserId(), command);
  }

  /** 获取当前用户账号安全信息 */
  public SecurityProfileResponse getSecurityProfile() {
    Long userId = tokenSessionPort.currentUserId();

    boolean hasPassword = authApplicationService.hasPassword(userId);
    List<ExternalAccountResponse> boundAccounts =
        externalAccountApplicationService.listByUserId(userId);
    List<LoginLogVO> recentLogins = loginLogService.getRecentLogins(userId, 10);

    return new SecurityProfileResponse(hasPassword, boundAccounts, recentLogins);
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

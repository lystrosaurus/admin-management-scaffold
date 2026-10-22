package io.github.lystrosaurus.admin.auth.adapter.response;

import io.github.lystrosaurus.admin.system.menu.adapter.response.MenuResponse;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserResponse;
import java.util.List;

/**
 * 用户资料响应（字段与旧 ProfileVO 完全一致，保证 API 契约不变）
 *
 * @param user 用户信息
 * @param roles 角色列表
 * @param permissions 权限列表
 * @param menus 菜单列表
 */
public record ProfileResponse(
    UserResponse user, List<String> roles, List<String> permissions, List<MenuResponse> menus) {}

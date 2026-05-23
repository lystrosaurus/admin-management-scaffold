package io.github.lystrosaurus.admin.auth.vo;

import io.github.lystrosaurus.admin.system.menu.vo.MenuVO;
import io.github.lystrosaurus.admin.system.user.vo.UserVO;
import java.util.List;

/**
 * 用户资料VO
 *
 * @param user 用户信息
 * @param roles 角色列表
 * @param permissions 权限列表
 * @param menus 菜单列表
 */
public record ProfileVO(
    UserVO user, List<String> roles, List<String> permissions, List<MenuVO> menus) {}

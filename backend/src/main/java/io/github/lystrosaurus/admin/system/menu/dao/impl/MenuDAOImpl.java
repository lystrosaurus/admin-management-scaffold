package io.github.lystrosaurus.admin.system.menu.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.github.lystrosaurus.admin.system.menu.dao.MenuDAO;
import io.github.lystrosaurus.admin.system.menu.entity.SysMenu;
import io.github.lystrosaurus.admin.system.menu.mapper.SysMenuMapper;
import io.github.lystrosaurus.admin.system.role.entity.SysRoleMenu;
import io.github.lystrosaurus.admin.system.role.mapper.SysRoleMenuMapper;
import io.github.lystrosaurus.admin.system.user.entity.SysUserRole;
import io.github.lystrosaurus.admin.system.user.mapper.SysUserRoleMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 菜单数据访问对象实现
 *
 * <p>使用 MyBatis-Plus Wrapper 构建查询条件。
 */
@Service
@RequiredArgsConstructor
public class MenuDAOImpl implements MenuDAO {

  private final SysMenuMapper menuMapper;
  private final SysRoleMenuMapper roleMenuMapper;
  private final SysUserRoleMapper userRoleMapper;

  @Override
  public SysMenu findById(Long id) {
    return menuMapper.selectById(id);
  }

  @Override
  public void save(SysMenu menu) {
    menuMapper.insert(menu);
  }

  @Override
  public void update(SysMenu menu) {
    menuMapper.updateById(menu);
  }

  @Override
  public void deleteById(Long id) {
    menuMapper.deleteById(id);
  }

  @Override
  public List<SysMenu> findAll() {
    return menuMapper.selectList(
        new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSortOrder));
  }

  @Override
  public List<SysMenu> findByParentId(Long parentId) {
    return menuMapper.selectList(
        new LambdaQueryWrapper<SysMenu>()
            .eq(SysMenu::getParentId, parentId)
            .orderByAsc(SysMenu::getSortOrder));
  }

  @Override
  public List<SysMenu> findByRoleId(Long roleId) {
    // 先查询角色菜单关联
    List<SysRoleMenu> roleMenus =
        roleMenuMapper.selectList(
            new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));

    if (roleMenus.isEmpty()) {
      return List.of();
    }

    // 再查询菜单详情
    List<Long> menuIds =
        roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    return menuMapper.selectList(
        new LambdaQueryWrapper<SysMenu>()
            .in(SysMenu::getId, menuIds)
            .orderByAsc(SysMenu::getSortOrder));
  }

  @Override
  public List<SysMenu> findByUserId(Long userId) {
    // 先查询用户角色关联
    List<SysUserRole> userRoles =
        userRoleMapper.selectList(
            new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));

    if (userRoles.isEmpty()) {
      return List.of();
    }

    // 再查询角色菜单关联
    List<Long> roleIds =
        userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    List<SysRoleMenu> roleMenus =
        roleMenuMapper.selectList(
            new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds));

    if (roleMenus.isEmpty()) {
      return List.of();
    }

    // 最后查询菜单详情
    List<Long> menuIds =
        roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    return menuMapper.selectList(
        new LambdaQueryWrapper<SysMenu>()
            .in(SysMenu::getId, menuIds)
            .orderByAsc(SysMenu::getSortOrder));
  }

  @Override
  public boolean existsByPermissionCode(String permissionCode) {
    return menuMapper.selectCount(
            new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getPermissionCode, permissionCode))
        > 0;
  }

  @Override
  public void updateStatus(Long id, Integer status) {
    menuMapper.update(
        null,
        new LambdaUpdateWrapper<SysMenu>().eq(SysMenu::getId, id).set(SysMenu::getStatus, status));
  }
}

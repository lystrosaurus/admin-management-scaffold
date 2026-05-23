package io.github.lystrosaurus.admin.system.role.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.system.menu.entity.SysMenu;
import io.github.lystrosaurus.admin.system.menu.mapper.SysMenuMapper;
import io.github.lystrosaurus.admin.system.permission.entity.SysPermission;
import io.github.lystrosaurus.admin.system.permission.mapper.SysPermissionMapper;
import io.github.lystrosaurus.admin.system.role.dao.RoleDAO;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import io.github.lystrosaurus.admin.system.role.entity.SysRoleMenu;
import io.github.lystrosaurus.admin.system.role.entity.SysRolePermission;
import io.github.lystrosaurus.admin.system.role.mapper.SysRoleMapper;
import io.github.lystrosaurus.admin.system.role.mapper.SysRoleMenuMapper;
import io.github.lystrosaurus.admin.system.role.mapper.SysRolePermissionMapper;
import io.github.lystrosaurus.admin.system.user.entity.SysUserRole;
import io.github.lystrosaurus.admin.system.user.mapper.SysUserRoleMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 角色数据访问对象实现
 *
 * <p>使用 MyBatis-Plus Wrapper 构建查询条件。
 */
@Service
@RequiredArgsConstructor
public class RoleDAOImpl implements RoleDAO {

  private final SysRoleMapper roleMapper;
  private final SysRolePermissionMapper rolePermissionMapper;
  private final SysRoleMenuMapper roleMenuMapper;
  private final SysPermissionMapper permissionMapper;
  private final SysMenuMapper menuMapper;
  private final SysUserRoleMapper userRoleMapper;

  @Override
  public SysRole findById(Long id) {
    return roleMapper.selectById(id);
  }

  @Override
  public SysRole findByCode(String code) {
    return roleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getCode, code));
  }

  @Override
  public void save(SysRole role) {
    roleMapper.insert(role);
  }

  @Override
  public void update(SysRole role) {
    roleMapper.updateById(role);
  }

  @Override
  public void deleteById(Long id) {
    roleMapper.deleteById(id);
  }

  @Override
  public List<SysRole> findByCondition(String keyword, String status, int page, int size) {
    Page<SysRole> pageParam = new Page<>(page, size);
    LambdaQueryWrapper<SysRole> wrapper = buildConditionWrapper(keyword, status);
    return roleMapper.selectPage(pageParam, wrapper).getRecords();
  }

  @Override
  public long countByCondition(String keyword, String status) {
    LambdaQueryWrapper<SysRole> wrapper = buildConditionWrapper(keyword, status);
    return roleMapper.selectCount(wrapper);
  }

  @Override
  public boolean existsByCode(String code) {
    return roleMapper.selectCount(new LambdaQueryWrapper<SysRole>().eq(SysRole::getCode, code)) > 0;
  }

  @Override
  public boolean existsByCodeAndIdNot(String code, Long id) {
    return roleMapper.selectCount(
            new LambdaQueryWrapper<SysRole>().eq(SysRole::getCode, code).ne(SysRole::getId, id))
        > 0;
  }

  @Override
  public List<SysPermission> findPermissionsByRoleId(Long roleId) {
    // 先查询角色权限关联
    List<SysRolePermission> rolePermissions =
        rolePermissionMapper.selectList(
            new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId));

    if (rolePermissions.isEmpty()) {
      return List.of();
    }

    // 再查询权限详情
    List<Long> permissionIds =
        rolePermissions.stream()
            .map(SysRolePermission::getPermissionId)
            .collect(Collectors.toList());
    return permissionMapper.selectList(
        new LambdaQueryWrapper<SysPermission>().in(SysPermission::getId, permissionIds));
  }

  @Override
  public void assignPermissions(Long roleId, List<Long> permissionIds) {
    if (permissionIds == null || permissionIds.isEmpty()) {
      return;
    }

    // 先移除现有权限
    removePermissions(roleId);

    // 再分配新权限
    for (Long permissionId : permissionIds) {
      SysRolePermission rolePermission = new SysRolePermission();
      rolePermission.setRoleId(roleId);
      rolePermission.setPermissionId(permissionId);
      rolePermissionMapper.insert(rolePermission);
    }
  }

  @Override
  public void removePermissions(Long roleId) {
    rolePermissionMapper.delete(
        new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId));
  }

  @Override
  public List<SysMenu> findMenusByRoleId(Long roleId) {
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
    return menuMapper.selectList(new LambdaQueryWrapper<SysMenu>().in(SysMenu::getId, menuIds));
  }

  @Override
  public void assignMenus(Long roleId, List<Long> menuIds) {
    if (menuIds == null || menuIds.isEmpty()) {
      return;
    }

    // 先移除现有菜单
    removeMenus(roleId);

    // 再分配新菜单
    for (Long menuId : menuIds) {
      SysRoleMenu roleMenu = new SysRoleMenu();
      roleMenu.setRoleId(roleId);
      roleMenu.setMenuId(menuId);
      roleMenuMapper.insert(roleMenu);
    }
  }

  @Override
  public void removeMenus(Long roleId) {
    roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
  }

  @Override
  public List<SysRole> findAll() {
    return roleMapper.selectList(
        new LambdaQueryWrapper<SysRole>().orderByAsc(SysRole::getSortOrder));
  }

  @Override
  public List<SysRole> findByUserId(Long userId) {
    // 先查询用户角色关联
    List<SysUserRole> userRoles =
        userRoleMapper.selectList(
            new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));

    if (userRoles.isEmpty()) {
      return List.of();
    }

    // 再查询角色详情
    List<Long> roleIds =
        userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    return roleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIds));
  }

  /**
   * 构建条件查询 Wrapper
   *
   * @param keyword 关键词（可选）
   * @param status 状态（可选）
   * @return LambdaQueryWrapper
   */
  private LambdaQueryWrapper<SysRole> buildConditionWrapper(String keyword, String status) {
    LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();

    if (StringUtils.hasText(keyword)) {
      wrapper.and(w -> w.like(SysRole::getCode, keyword).or().like(SysRole::getName, keyword));
    }

    if (StringUtils.hasText(status)) {
      wrapper.eq(SysRole::getStatus, status);
    }

    wrapper.orderByDesc(SysRole::getCreatedAt);
    return wrapper;
  }
}

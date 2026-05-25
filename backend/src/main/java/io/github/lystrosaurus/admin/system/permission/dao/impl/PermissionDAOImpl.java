package io.github.lystrosaurus.admin.system.permission.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.system.permission.dao.PermissionDAO;
import io.github.lystrosaurus.admin.system.permission.entity.SysPermission;
import io.github.lystrosaurus.admin.system.permission.mapper.SysPermissionMapper;
import io.github.lystrosaurus.admin.system.role.entity.SysRolePermission;
import io.github.lystrosaurus.admin.system.role.mapper.SysRolePermissionMapper;
import io.github.lystrosaurus.admin.system.user.entity.SysUserRole;
import io.github.lystrosaurus.admin.system.user.mapper.SysUserRoleMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 权限数据访问对象实现
 *
 * <p>使用 MyBatis-Plus Wrapper 构建查询条件。
 */
@Service
@RequiredArgsConstructor
public class PermissionDAOImpl implements PermissionDAO {

  private final SysPermissionMapper permissionMapper;
  private final SysRolePermissionMapper rolePermissionMapper;
  private final SysUserRoleMapper userRoleMapper;

  @Override
  public SysPermission findById(Long id) {
    return permissionMapper.selectById(id);
  }

  @Override
  public SysPermission findByCode(String code) {
    return permissionMapper.selectOne(
        new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getCode, code));
  }

  @Override
  public void save(SysPermission permission) {
    permissionMapper.insert(permission);
  }

  @Override
  public void update(SysPermission permission) {
    permissionMapper.updateById(permission);
  }

  @Override
  public void deleteById(Long id) {
    permissionMapper.deleteById(id);
  }

  @Override
  public List<SysPermission> findByCondition(String module, String type, int page, int size) {
    Page<SysPermission> pageParam = new Page<>(page, size);
    LambdaQueryWrapper<SysPermission> wrapper = buildConditionWrapper(module, type);
    return permissionMapper.selectPage(pageParam, wrapper).getRecords();
  }

  @Override
  public long countByCondition(String module, String type) {
    LambdaQueryWrapper<SysPermission> wrapper = buildConditionWrapper(module, type);
    return permissionMapper.selectCount(wrapper);
  }

  @Override
  public List<SysPermission> findByModule(String module) {
    return permissionMapper.selectList(
        new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getModule, module));
  }

  @Override
  public List<SysPermission> findByRoleId(Long roleId) {
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
  public List<SysPermission> findByUserId(Long userId) {
    // 先查询用户角色关联
    List<SysUserRole> userRoles =
        userRoleMapper.selectList(
            new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));

    if (userRoles.isEmpty()) {
      return List.of();
    }

    // 再查询角色权限关联
    List<Long> roleIds =
        userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    List<SysRolePermission> rolePermissions =
        rolePermissionMapper.selectList(
            new LambdaQueryWrapper<SysRolePermission>().in(SysRolePermission::getRoleId, roleIds));

    if (rolePermissions.isEmpty()) {
      return List.of();
    }

    // 最后查询权限详情
    List<Long> permissionIds =
        rolePermissions.stream()
            .map(SysRolePermission::getPermissionId)
            .collect(Collectors.toList());
    return permissionMapper.selectList(
        new LambdaQueryWrapper<SysPermission>().in(SysPermission::getId, permissionIds));
  }

  @Override
  public boolean existsByCode(String code) {
    return permissionMapper.selectCount(
            new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getCode, code))
        > 0;
  }

  @Override
  public boolean existsByCodeAndIdNot(String code, Long id) {
    return permissionMapper.selectCount(
            new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getCode, code)
                .ne(SysPermission::getId, id))
        > 0;
  }

  @Override
  public List<SysPermission> findAll() {
    return permissionMapper.selectList(
        new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getSortOrder));
  }

  /**
   * 构建条件查询 Wrapper
   *
   * @param module 模块（可选）
   * @param type 类型（可选）
   * @return LambdaQueryWrapper
   */
  private LambdaQueryWrapper<SysPermission> buildConditionWrapper(String module, String type) {
    LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();

    if (StringUtils.hasText(module)) {
      wrapper.eq(SysPermission::getModule, module);
    }

    if (StringUtils.hasText(type)) {
      wrapper.eq(SysPermission::getType, type);
    }

    wrapper.orderByAsc(SysPermission::getSortOrder);
    return wrapper;
  }
}

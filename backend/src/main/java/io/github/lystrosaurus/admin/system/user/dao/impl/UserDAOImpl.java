package io.github.lystrosaurus.admin.system.user.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import io.github.lystrosaurus.admin.system.role.mapper.SysRoleMapper;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import io.github.lystrosaurus.admin.system.user.entity.SysUserRole;
import io.github.lystrosaurus.admin.system.user.mapper.SysUserMapper;
import io.github.lystrosaurus.admin.system.user.mapper.SysUserRoleMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户数据访问对象实现
 *
 * <p>使用 MyBatis-Plus Wrapper 构建查询条件。
 */
@Service
@RequiredArgsConstructor
public class UserDAOImpl implements UserDAO {

  private final SysUserMapper userMapper;
  private final SysUserRoleMapper userRoleMapper;
  private final SysRoleMapper roleMapper;

  @Override
  public SysUser findById(Long id) {
    return userMapper.selectById(id);
  }

  @Override
  public SysUser findByUsername(String username) {
    return userMapper.selectOne(
        new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
  }

  @Override
  public void save(SysUser user) {
    userMapper.insert(user);
  }

  @Override
  public void update(SysUser user) {
    userMapper.updateById(user);
  }

  @Override
  public void deleteById(Long id) {
    userMapper.deleteById(id);
  }

  @Override
  public List<SysUser> findByCondition(String username, String status, int page, int size) {
    // page 参数约定为 1-based（与 MyBatis-Plus Page 一致）
    Page<SysUser> pageParam = new Page<>(page, size);
    LambdaQueryWrapper<SysUser> wrapper = buildConditionWrapper(username, status);
    return userMapper.selectPage(pageParam, wrapper).getRecords();
  }

  @Override
  public long countByCondition(String username, String status) {
    LambdaQueryWrapper<SysUser> wrapper = buildConditionWrapper(username, status);
    return userMapper.selectCount(wrapper);
  }

  @Override
  public boolean existsByUsername(String username) {
    return userMapper.selectCount(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username))
        > 0;
  }

  @Override
  public boolean existsByUsernameAndIdNot(String username, Long id) {
    return userMapper.selectCount(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .ne(SysUser::getId, id))
        > 0;
  }

  @Override
  public List<SysRole> findRolesByUserId(Long userId) {
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

  @Override
  public void assignRoles(Long userId, List<Long> roleIds) {
    if (roleIds == null || roleIds.isEmpty()) {
      return;
    }

    // 先移除现有角色
    removeRoles(userId);

    // 再分配新角色
    for (Long roleId : roleIds) {
      SysUserRole userRole = new SysUserRole();
      userRole.setUserId(userId);
      userRole.setRoleId(roleId);
      userRoleMapper.insert(userRole);
    }
  }

  @Override
  public void removeRoles(Long userId) {
    userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
  }

  /**
   * 构建条件查询 Wrapper
   *
   * @param username 用户名（可选）
   * @param status 状态（可选）
   * @return LambdaQueryWrapper
   */
  private LambdaQueryWrapper<SysUser> buildConditionWrapper(String username, String status) {
    LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

    if (StringUtils.hasText(username)) {
      wrapper.like(SysUser::getUsername, username);
    }

    if (StringUtils.hasText(status)) {
      wrapper.eq(SysUser::getStatus, status);
    }

    wrapper.orderByDesc(SysUser::getCreatedAt);
    return wrapper;
  }
}

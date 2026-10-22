package io.github.lystrosaurus.admin.system.user.infrastructure.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import io.github.lystrosaurus.admin.system.role.mapper.SysRoleMapper;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserQueryRepository;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRoleInfo;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.entity.SysUserRole;
import io.github.lystrosaurus.admin.system.user.infrastructure.converter.UserPoConverter;
import io.github.lystrosaurus.admin.system.user.infrastructure.persistence.mapper.UserMpMapper;
import io.github.lystrosaurus.admin.system.user.infrastructure.persistence.po.UserPO;
import io.github.lystrosaurus.admin.system.user.mapper.SysUserRoleMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/** 用户查询端口的 MyBatis-Plus 实现（读模型侧）。 */
@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

  private final UserMpMapper userMpMapper;
  private final SysUserRoleMapper userRoleMapper;
  private final SysRoleMapper roleMapper;
  private final UserPoConverter converter;

  @Override
  public Page<User> findPage(String username, String status, PageQuery pageQuery) {
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserPO> mpPage =
        new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
            pageQuery.pageNum(), pageQuery.pageSize());
    LambdaQueryWrapper<UserPO> wrapper = buildConditionWrapper(username, status);
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserPO> result =
        userMpMapper.selectPage(mpPage, wrapper);
    List<User> records = result.getRecords().stream().map(converter::toDomain).toList();
    return new Page<>(records, result.getTotal(), pageQuery.pageNum(), pageQuery.pageSize());
  }

  @Override
  public long countByCondition(String username, String status) {
    return userMpMapper.selectCount(buildConditionWrapper(username, status));
  }

  @Override
  public List<UserRoleInfo> findRolesByUserId(UserId userId) {
    // 先查询用户角色关联
    List<SysUserRole> userRoles =
        userRoleMapper.selectList(
            new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId.value()));

    if (userRoles.isEmpty()) {
      return List.of();
    }

    // 再查询角色详情
    List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).toList();
    return roleMapper
        .selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIds))
        .stream()
        .map(
            role ->
                new UserRoleInfo(
                    role.getId(),
                    role.getCode(),
                    role.getName(),
                    role.getDescription(),
                    role.getStatus(),
                    role.getDataScopeType()))
        .toList();
  }

  /**
   * 构建条件查询 Wrapper
   *
   * @param username 用户名（可选）
   * @param status 状态（可选）
   * @return LambdaQueryWrapper
   */
  private LambdaQueryWrapper<UserPO> buildConditionWrapper(String username, String status) {
    LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();

    if (StringUtils.hasText(username)) {
      wrapper.like(UserPO::getUsername, username);
    }

    if (StringUtils.hasText(status)) {
      wrapper.eq(UserPO::getStatus, status);
    }

    wrapper.orderByDesc(UserPO::getCreatedAt);
    return wrapper;
  }
}

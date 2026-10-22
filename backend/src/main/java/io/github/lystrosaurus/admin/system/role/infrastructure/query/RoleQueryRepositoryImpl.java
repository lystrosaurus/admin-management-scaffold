package io.github.lystrosaurus.admin.system.role.infrastructure.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleQueryRepository;
import io.github.lystrosaurus.admin.system.role.domain.model.Role;
import io.github.lystrosaurus.admin.system.role.infrastructure.converter.RolePoConverter;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.RoleMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.UserRoleMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RolePO;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.UserRolePO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/** 角色查询端口的 MyBatis-Plus 实现（读模型侧）。 */
@Repository
@RequiredArgsConstructor
public class RoleQueryRepositoryImpl implements RoleQueryRepository {

  private final RoleMpMapper roleMpMapper;
  private final UserRoleMpMapper userRoleMpMapper;
  private final RolePoConverter converter;

  @Override
  public Page<Role> findPage(String keyword, String status, PageQuery pageQuery) {
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<RolePO> mpPage =
        new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
            pageQuery.pageNum(), pageQuery.pageSize());
    LambdaQueryWrapper<RolePO> wrapper = buildConditionWrapper(keyword, status);
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<RolePO> result =
        roleMpMapper.selectPage(mpPage, wrapper);
    List<Role> records = result.getRecords().stream().map(converter::toDomain).toList();
    return new Page<>(records, result.getTotal(), pageQuery.pageNum(), pageQuery.pageSize());
  }

  @Override
  public long countByCondition(String keyword, String status) {
    return roleMpMapper.selectCount(buildConditionWrapper(keyword, status));
  }

  @Override
  public List<Role> findAll() {
    return roleMpMapper
        .selectList(new LambdaQueryWrapper<RolePO>().orderByAsc(RolePO::getSortOrder))
        .stream()
        .map(converter::toDomain)
        .toList();
  }

  @Override
  public List<Role> findByUserId(Long userId) {
    List<Long> roleIds =
        userRoleMpMapper
            .selectList(new LambdaQueryWrapper<UserRolePO>().eq(UserRolePO::getUserId, userId))
            .stream()
            .map(UserRolePO::getRoleId)
            .toList();
    if (roleIds.isEmpty()) {
      return List.of();
    }
    return roleMpMapper
        .selectList(new LambdaQueryWrapper<RolePO>().in(RolePO::getId, roleIds))
        .stream()
        .map(converter::toDomain)
        .toList();
  }

  /**
   * 构建条件查询 Wrapper
   *
   * @param keyword 关键词（匹配编码/名称）
   * @param status 状态（可选）
   * @return LambdaQueryWrapper
   */
  private LambdaQueryWrapper<RolePO> buildConditionWrapper(String keyword, String status) {
    LambdaQueryWrapper<RolePO> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.hasText(keyword)) {
      wrapper.and(w -> w.like(RolePO::getCode, keyword).or().like(RolePO::getName, keyword));
    }
    if (StringUtils.hasText(status)) {
      wrapper.eq(RolePO::getStatus, status);
    }
    wrapper.orderByDesc(RolePO::getCreatedAt);
    return wrapper;
  }
}

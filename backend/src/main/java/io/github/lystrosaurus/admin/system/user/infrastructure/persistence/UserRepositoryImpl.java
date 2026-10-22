package io.github.lystrosaurus.admin.system.user.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.GenericRepositoryImpl;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.entity.SysUserRole;
import io.github.lystrosaurus.admin.system.user.infrastructure.converter.UserPoConverter;
import io.github.lystrosaurus.admin.system.user.infrastructure.persistence.mapper.UserMpMapper;
import io.github.lystrosaurus.admin.system.user.infrastructure.persistence.po.UserPO;
import io.github.lystrosaurus.admin.system.user.mapper.SysUserRoleMapper;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

/** 用户仓储的 MyBatis-Plus 实现。 */
@Repository
public class UserRepositoryImpl extends GenericRepositoryImpl<User, UserId, UserPO, UserMpMapper>
    implements UserRepository {

  private final UserPoConverter converter;
  private final SysUserRoleMapper userRoleMapper;
  private final SqlSessionFactory sqlSessionFactory;

  public UserRepositoryImpl(
      UserMpMapper mapper,
      DomainEventPublisher eventPublisher,
      UserPoConverter converter,
      SysUserRoleMapper userRoleMapper,
      SqlSessionFactory sqlSessionFactory) {
    super(mapper, eventPublisher);
    this.converter = converter;
    this.userRoleMapper = userRoleMapper;
    this.sqlSessionFactory = sqlSessionFactory;
  }

  @Override
  protected UserPO toPo(User domain) {
    return converter.toPo(domain);
  }

  @Override
  protected User toDomain(UserPO po) {
    return converter.toDomain(po);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return Optional.ofNullable(
            mapper.selectOne(new LambdaQueryWrapper<UserPO>().eq(UserPO::getUsername, username)))
        .map(this::toDomain);
  }

  @Override
  public boolean existsByUsername(String username) {
    return mapper.selectCount(
            new LambdaQueryWrapper<UserPO>().eq(UserPO::getUsername, username))
        > 0;
  }

  @Override
  public boolean existsByUsernameAndIdNot(String username, UserId id) {
    return mapper.selectCount(
            new LambdaQueryWrapper<UserPO>()
                .eq(UserPO::getUsername, username)
                .ne(UserPO::getId, id.value()))
        > 0;
  }

  @Override
  public Optional<User> findByEmployeeId(Long employeeId) {
    return Optional.ofNullable(
            mapper.selectOne(
                new LambdaQueryWrapper<UserPO>().eq(UserPO::getEmployeeId, employeeId)))
        .map(this::toDomain);
  }

  @Override
  public List<User> findByIds(Collection<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    return mapper.selectBatchIds(ids).stream().map(this::toDomain).toList();
  }

  @Override
  public void assignRoles(Long userId, List<Long> roleIds) {
    if (roleIds == null || roleIds.isEmpty()) {
      return;
    }

    // 先移除现有角色
    removeRoles(userId);

    // 批量分配新角色
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
      SysUserRoleMapper batchMapper = sqlSession.getMapper(SysUserRoleMapper.class);
      for (Long roleId : roleIds) {
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        batchMapper.insert(userRole);
      }
      sqlSession.commit();
    }
  }

  @Override
  public void removeRoles(Long userId) {
    userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
  }
}

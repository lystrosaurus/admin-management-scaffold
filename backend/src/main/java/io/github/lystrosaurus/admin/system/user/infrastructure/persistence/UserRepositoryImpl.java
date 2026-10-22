package io.github.lystrosaurus.admin.system.user.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.GenericRepositoryImpl;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleAssociationPort;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.infrastructure.converter.UserPoConverter;
import io.github.lystrosaurus.admin.system.user.infrastructure.persistence.mapper.UserMpMapper;
import io.github.lystrosaurus.admin.system.user.infrastructure.persistence.po.UserPO;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** 用户仓储的 MyBatis-Plus 实现。 */
@Repository
public class UserRepositoryImpl extends GenericRepositoryImpl<User, UserId, UserPO, UserMpMapper>
    implements UserRepository {

  private final UserPoConverter converter;
  private final RoleAssociationPort roleAssociationPort;

  public UserRepositoryImpl(
      UserMpMapper mapper,
      DomainEventPublisher eventPublisher,
      UserPoConverter converter,
      RoleAssociationPort roleAssociationPort) {
    super(mapper, eventPublisher);
    this.converter = converter;
    this.roleAssociationPort = roleAssociationPort;
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
    roleAssociationPort.assignRoles(userId, roleIds);
  }

  @Override
  public void removeRoles(Long userId) {
    roleAssociationPort.removeRoles(userId);
  }
}

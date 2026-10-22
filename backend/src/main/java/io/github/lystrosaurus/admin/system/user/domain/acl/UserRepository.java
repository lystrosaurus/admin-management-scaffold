package io.github.lystrosaurus.admin.system.user.domain.acl;

import io.github.lystrosaurus.admin.shared.domain.acl.GenericRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** 用户仓储端口：用户聚合的存取契约。 */
public interface UserRepository extends GenericRepository<User, UserId> {

  /** 按用户名查找用户 */
  Optional<User> findByUsername(String username);

  /** 判断用户名是否存在 */
  boolean existsByUsername(String username);

  /** 判断用户名是否存在且排除指定用户 */
  boolean existsByUsernameAndIdNot(String username, UserId id);

  /** 按绑定员工ID查找用户 */
  Optional<User> findByEmployeeId(Long employeeId);

  /** 按ID集合批量查找用户 */
  List<User> findByIds(Collection<Long> ids);

  /** 分配角色给用户（波次2 随 role 上下文迁移） */
  void assignRoles(Long userId, List<Long> roleIds);

  /** 移除用户的所有角色（波次2 随 role 上下文迁移） */
  void removeRoles(Long userId);
}

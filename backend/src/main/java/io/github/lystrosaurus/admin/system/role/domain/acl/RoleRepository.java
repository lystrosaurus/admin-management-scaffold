package io.github.lystrosaurus.admin.system.role.domain.acl;

import io.github.lystrosaurus.admin.shared.domain.acl.GenericRepository;
import io.github.lystrosaurus.admin.system.role.domain.model.Role;
import io.github.lystrosaurus.admin.system.role.domain.model.RoleId;
import java.util.List;
import java.util.Optional;

/** 角色仓储端口：角色聚合的存取契约。 */
public interface RoleRepository extends GenericRepository<Role, RoleId> {

  /** 按角色编码查找角色 */
  Optional<Role> findByCode(String code);

  /** 判断角色编码是否存在 */
  boolean existsByCode(String code);

  /** 判断角色编码是否存在且排除指定角色 */
  boolean existsByCodeAndIdNot(String code, RoleId id);

  /** 全量替换角色权限关联（先清空再批量插入） */
  void replacePermissions(RoleId roleId, List<Long> permissionIds);

  /** 全量替换角色菜单关联（先清空再批量插入） */
  void replaceMenus(RoleId roleId, List<Long> menuIds);
}

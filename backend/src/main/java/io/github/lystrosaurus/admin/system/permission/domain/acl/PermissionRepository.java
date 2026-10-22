package io.github.lystrosaurus.admin.system.permission.domain.acl;

import io.github.lystrosaurus.admin.shared.domain.acl.GenericRepository;
import io.github.lystrosaurus.admin.system.permission.domain.model.Permission;
import io.github.lystrosaurus.admin.system.permission.domain.model.PermissionId;
import java.util.Optional;

/** 权限仓储端口：权限聚合的存取契约。 */
public interface PermissionRepository extends GenericRepository<Permission, PermissionId> {

  /** 按权限编码查找权限 */
  Optional<Permission> findByCode(String code);

  /** 判断权限编码是否存在 */
  boolean existsByCode(String code);

  /** 判断权限编码是否存在且排除指定权限 */
  boolean existsByCodeAndIdNot(String code, PermissionId id);
}

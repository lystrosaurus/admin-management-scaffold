package io.github.lystrosaurus.admin.system.permission.domain.acl;

import io.github.lystrosaurus.admin.system.permission.domain.model.Permission;
import java.util.List;

/** 权限查询端口：读模型侧查询。 */
public interface PermissionQueryRepository {

  /** 查询全部权限（按排序号升序） */
  List<Permission> findAll();

  /** 查询角色拥有的权限 */
  List<Permission> findByRoleId(Long roleId);

  /** 查询用户拥有的权限 */
  List<Permission> findByUserId(Long userId);
}

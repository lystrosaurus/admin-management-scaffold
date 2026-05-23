package io.github.lystrosaurus.admin.system.role.entity;

import io.github.lystrosaurus.admin.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/** 角色权限关联实体 */
@Getter
@Setter
public class SysRolePermission extends BaseEntity {

  /** 角色ID */
  private Long roleId;

  /** 权限ID */
  private Long permissionId;
}

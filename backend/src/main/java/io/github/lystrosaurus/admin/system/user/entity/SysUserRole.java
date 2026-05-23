package io.github.lystrosaurus.admin.system.user.entity;

import io.github.lystrosaurus.admin.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/** 用户角色关联实体 */
@Getter
@Setter
public class SysUserRole extends BaseEntity {

  /** 用户ID */
  private Long userId;

  /** 角色ID */
  private Long roleId;
}

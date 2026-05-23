package io.github.lystrosaurus.admin.system.role.entity;

import io.github.lystrosaurus.admin.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/** 角色菜单关联实体 */
@Getter
@Setter
public class SysRoleMenu extends BaseEntity {

  /** 角色ID */
  private Long roleId;

  /** 菜单ID */
  private Long menuId;
}

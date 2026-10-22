package io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po;

import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BaseAssociationPO;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色-组织关联持久化对象：映射 sys_role_org 表。
 *
 * <p>该表无审计、逻辑删除与乐观锁列，故继承 {@link BaseAssociationPO}，由注解 Mapper 显式读写。
 */
@Getter
@Setter
public class RoleOrgPO extends BaseAssociationPO {

  /** 角色ID */
  private Long roleId;

  /** 组织ID */
  private Long orgId;
}

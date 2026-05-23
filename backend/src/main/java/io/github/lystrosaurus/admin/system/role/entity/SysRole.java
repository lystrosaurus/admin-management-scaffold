package io.github.lystrosaurus.admin.system.role.entity;

import io.github.lystrosaurus.admin.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/** 角色实体 */
@Getter
@Setter
public class SysRole extends BaseEntity {

  /** 角色编码(唯一) */
  private String code;

  /** 角色名称 */
  private String name;

  /** 描述 */
  private String description;

  /** 排序号 */
  private Integer sortOrder;

  /** 状态(ENABLED/DISABLED) */
  private String status;

  /** 数据权限范围(ALL/ORG_TREE/ORG_ONLY/SELF/CUSTOM) */
  private String dataScopeType;
}

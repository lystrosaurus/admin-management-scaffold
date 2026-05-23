package io.github.lystrosaurus.admin.organization.orgunit.entity;

import io.github.lystrosaurus.admin.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/** 组织单元实体 */
@Getter
@Setter
public class OrgUnit extends BaseEntity {

  /** 父节点ID(0表示根节点) */
  private Long parentId;

  /** 组织编码 */
  private String code;

  /** 组织名称 */
  private String name;

  /** 完整路径(如/1/5/12/) */
  private String fullPath;

  /** 层级(从1开始) */
  private Integer level;

  /** 负责人员工ID */
  private Long managerEmployeeId;

  /** 排序号 */
  private Integer sortOrder;

  /** 状态(ENABLED/DISABLED) */
  private String status;

  /** 数据来源(MANUAL/IMPORT/API) */
  private String sourceType;
}

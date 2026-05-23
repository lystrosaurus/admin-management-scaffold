package io.github.lystrosaurus.admin.organization.employee.entity;

import io.github.lystrosaurus.admin.entity.BaseEntity;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/** 员工-组织关联实体 */
@Getter
@Setter
public class EmployeeOrg extends BaseEntity {

  /** 员工ID */
  private Long employeeId;

  /** 组织ID */
  private Long orgId;

  /** 是否主组织(0-否，1-是) */
  private Integer isPrimary;

  /** 岗位名称 */
  private String positionName;

  /** 开始日期 */
  private LocalDate startDate;

  /** 结束日期 */
  private LocalDate endDate;

  /** 状态(ACTIVE/INACTIVE) */
  private String status;
}

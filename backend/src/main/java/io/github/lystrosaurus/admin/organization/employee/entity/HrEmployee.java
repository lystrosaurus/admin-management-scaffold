package io.github.lystrosaurus.admin.organization.employee.entity;

import io.github.lystrosaurus.admin.entity.BaseEntity;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/** 员工实体 */
@Getter
@Setter
public class HrEmployee extends BaseEntity {

  /** 员工工号 */
  private String employeeNo;

  /** 员工姓名 */
  private String name;

  /** 常用名/花名 */
  private String preferredName;

  /** 手机号 */
  private String mobile;

  /** 邮箱 */
  private String email;

  /** 主组织ID */
  private Long primaryOrgId;

  /** 职位 */
  private String jobTitle;

  /** 在职状态(ACTIVE/RESIGNED/SUSPENDED) */
  private String employmentStatus;

  /** 入职日期 */
  private LocalDate entryDate;

  /** 离职日期 */
  private LocalDate leaveDate;

  /** 数据来源(MANUAL/IMPORT/API) */
  private String sourceType;
}

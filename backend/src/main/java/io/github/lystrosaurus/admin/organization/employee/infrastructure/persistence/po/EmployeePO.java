package io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 员工持久化对象（hr_employee）。 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("hr_employee")
public class EmployeePO extends BasePO {

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

  /** 关联系统用户ID（sys_user.id），可空 */
  private Long userId;

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

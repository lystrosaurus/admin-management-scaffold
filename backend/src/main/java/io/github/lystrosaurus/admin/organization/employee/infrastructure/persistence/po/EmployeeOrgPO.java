package io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 员工-组织关联持久化对象（employee_org）。 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("employee_org")
public class EmployeeOrgPO extends BasePO {

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

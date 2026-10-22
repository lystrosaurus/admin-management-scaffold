package io.github.lystrosaurus.admin.organization.employee.infrastructure.converter;

import io.github.lystrosaurus.admin.organization.employee.domain.model.Employee;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeId;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeOrgAssignment;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.po.EmployeeOrgPO;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.po.EmployeePO;
import org.springframework.stereotype.Component;

/** 员工聚合 ↔ 持久化对象转换器。审计字段由 MyBatis-Plus 自动填充，不在此转换。 */
@Component
public class EmployeePoConverter {

  /** 领域对象转持久化对象 */
  public EmployeePO toPo(Employee employee) {
    EmployeePO po = new EmployeePO();
    po.setId(employee.id() == null ? null : employee.id().value());
    po.setEmployeeNo(employee.employeeNo());
    po.setName(employee.name());
    po.setPreferredName(employee.preferredName());
    po.setMobile(employee.mobile());
    po.setEmail(employee.email());
    po.setPrimaryOrgId(employee.primaryOrgId());
    po.setUserId(employee.userId());
    po.setJobTitle(employee.jobTitle());
    po.setEmploymentStatus(employee.employmentStatus());
    po.setEntryDate(employee.entryDate());
    po.setLeaveDate(employee.leaveDate());
    po.setSourceType(employee.sourceType());
    po.setVersion(employee.version());
    return po;
  }

  /** 持久化对象转领域对象（重建聚合，不登记事件） */
  public Employee toDomain(EmployeePO po) {
    return Employee.restore(
        EmployeeId.of(po.getId()),
        po.getEmployeeNo(),
        po.getName(),
        po.getPreferredName(),
        po.getMobile(),
        po.getEmail(),
        po.getPrimaryOrgId(),
        po.getUserId(),
        po.getJobTitle(),
        po.getEmploymentStatus(),
        po.getEntryDate(),
        po.getLeaveDate(),
        po.getSourceType(),
        po.getCreatedAt(),
        po.getVersion());
  }

  /** 员工-组织关联持久化对象转读模型 */
  public EmployeeOrgAssignment toAssignment(EmployeeOrgPO po) {
    return new EmployeeOrgAssignment(
        po.getId(),
        po.getOrgId(),
        po.getIsPrimary(),
        po.getPositionName(),
        po.getStartDate(),
        po.getEndDate(),
        po.getStatus());
  }
}

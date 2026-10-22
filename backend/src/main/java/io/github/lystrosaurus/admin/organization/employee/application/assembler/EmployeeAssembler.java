package io.github.lystrosaurus.admin.organization.employee.application.assembler;

import io.github.lystrosaurus.admin.organization.employee.adapter.response.EmployeeDetailResponse;
import io.github.lystrosaurus.admin.organization.employee.adapter.response.EmployeeOrgResponse;
import io.github.lystrosaurus.admin.organization.employee.adapter.response.EmployeeResponse;
import io.github.lystrosaurus.admin.organization.employee.domain.model.Employee;
import java.util.List;
import org.springframework.stereotype.Component;

/** 员工装配器：聚合 → 响应模型。 */
@Component
public class EmployeeAssembler {

  /** 聚合转员工响应 */
  public EmployeeResponse toResponse(Employee employee, String orgUnitName, String username) {
    return new EmployeeResponse(
        employee.id().value(),
        employee.employeeNo(),
        employee.name(),
        employee.preferredName(),
        employee.mobile(),
        employee.email(),
        employee.jobTitle(),
        employee.employmentStatus(),
        employee.primaryOrgId(),
        orgUnitName,
        employee.entryDate(),
        employee.leaveDate(),
        employee.createdAt(),
        employee.userId(),
        username);
  }

  /** 聚合与组织关联转员工详情响应 */
  public EmployeeDetailResponse toDetailResponse(
      Employee employee,
      String orgUnitName,
      List<EmployeeOrgResponse> orgs,
      String username) {
    return new EmployeeDetailResponse(
        employee.id().value(),
        employee.employeeNo(),
        employee.name(),
        employee.preferredName(),
        employee.mobile(),
        employee.email(),
        employee.jobTitle(),
        employee.employmentStatus(),
        employee.primaryOrgId(),
        orgUnitName,
        employee.entryDate(),
        employee.leaveDate(),
        employee.createdAt(),
        orgs,
        employee.userId(),
        username);
  }
}

package io.github.lystrosaurus.admin.organization.employee.domain.acl;

import io.github.lystrosaurus.admin.organization.employee.domain.model.Employee;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeId;
import io.github.lystrosaurus.admin.shared.domain.acl.GenericRepository;

/** 员工仓储端口：员工聚合的存取契约。 */
public interface EmployeeRepository extends GenericRepository<Employee, EmployeeId> {

  /** 判断工号是否存在 */
  boolean existsByEmployeeNo(String employeeNo);

  /** 判断工号是否存在且排除指定员工 */
  boolean existsByEmployeeNoAndIdNot(String employeeNo, EmployeeId id);
}

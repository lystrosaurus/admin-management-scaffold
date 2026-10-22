package io.github.lystrosaurus.admin.organization.employee.domain.acl;

import io.github.lystrosaurus.admin.organization.employee.domain.model.Employee;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeOrgAssignment;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** 员工查询端口：面向读模型的查询契约（CQRS 查询侧）。 */
public interface EmployeeQueryRepository {

  /** 按工号查找员工 */
  Optional<Employee> findByEmployeeNo(String employeeNo);

  /** 按条件分页查询员工 */
  Page<Employee> findPage(
      String keyword, String employmentStatus, Long primaryOrgId, PageQuery pageQuery);

  /** 按条件统计员工数量 */
  long countByCondition(String keyword, String employmentStatus, Long primaryOrgId);

  /** 按ID集合批量查找员工 */
  List<Employee> findByIds(Collection<Long> ids);

  /** 查询员工的全部组织关联 */
  List<EmployeeOrgAssignment> findOrgsByEmployeeId(Long employeeId);
}

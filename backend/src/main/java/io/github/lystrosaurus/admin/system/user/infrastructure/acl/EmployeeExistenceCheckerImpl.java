package io.github.lystrosaurus.admin.system.user.infrastructure.acl;

import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeDAO;
import io.github.lystrosaurus.admin.system.user.domain.acl.EmployeeExistenceChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 员工存在性检查的防腐层实现：委托 organization 上下文的 EmployeeDAO。 */
@Component
@RequiredArgsConstructor
public class EmployeeExistenceCheckerImpl implements EmployeeExistenceChecker {

  private final EmployeeDAO employeeDAO;

  @Override
  public boolean exists(Long employeeId) {
    return employeeDAO.existsById(employeeId);
  }
}

package io.github.lystrosaurus.admin.system.user.infrastructure.acl;

import io.github.lystrosaurus.admin.organization.employee.domain.acl.EmployeeQueryPort;
import io.github.lystrosaurus.admin.system.user.domain.acl.EmployeeExistenceChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 员工存在性检查的防腐层实现：委托 organization 上下文暴露的查询端口。 */
@Component
@RequiredArgsConstructor
public class EmployeeExistenceCheckerImpl implements EmployeeExistenceChecker {

  private final EmployeeQueryPort employeeQueryPort;

  @Override
  public boolean exists(Long employeeId) {
    return employeeQueryPort.existsById(employeeId);
  }
}

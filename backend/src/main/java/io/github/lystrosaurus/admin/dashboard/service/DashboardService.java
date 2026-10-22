package io.github.lystrosaurus.admin.dashboard.service;

import io.github.lystrosaurus.admin.dashboard.vo.DashboardVO;
import io.github.lystrosaurus.admin.organization.employee.domain.acl.EmployeeQueryPort;
import io.github.lystrosaurus.admin.organization.orgunit.domain.acl.OrgUnitQueryPort;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleQueryRepository;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 仪表盘服务实现 */
@Service
@RequiredArgsConstructor
public class DashboardService {

  private final UserQueryRepository userQueryRepository;
  private final RoleQueryRepository roleQueryRepository;
  private final EmployeeQueryPort employeeQueryPort;
  private final OrgUnitQueryPort orgUnitQueryPort;

  public DashboardVO getStats() {
    long userCount = userQueryRepository.countByCondition(null, null);
    long roleCount = roleQueryRepository.countByCondition(null, null);
    long employeeCount = employeeQueryPort.countAll();
    long orgUnitCount = orgUnitQueryPort.countAll();
    return new DashboardVO(userCount, roleCount, employeeCount, orgUnitCount);
  }
}

package io.github.lystrosaurus.admin.dashboard.application;

import io.github.lystrosaurus.admin.dashboard.adapter.response.DashboardResponse;
import io.github.lystrosaurus.admin.organization.employee.domain.acl.EmployeeQueryPort;
import io.github.lystrosaurus.admin.organization.orgunit.domain.acl.OrgUnitQueryPort;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleQueryRepository;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 仪表盘应用服务：聚合各上下文查询端口输出统计数据。 */
@Service
@RequiredArgsConstructor
public class DashboardApplicationService {

  private final UserQueryRepository userQueryRepository;
  private final RoleQueryRepository roleQueryRepository;
  private final EmployeeQueryPort employeeQueryPort;
  private final OrgUnitQueryPort orgUnitQueryPort;

  /** 获取系统统计数据 */
  public DashboardResponse getStats() {
    long userCount = userQueryRepository.countByCondition(null, null);
    long roleCount = roleQueryRepository.countByCondition(null, null);
    long employeeCount = employeeQueryPort.countAll();
    long orgUnitCount = orgUnitQueryPort.countAll();
    return new DashboardResponse(userCount, roleCount, employeeCount, orgUnitCount);
  }
}

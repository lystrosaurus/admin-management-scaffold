package io.github.lystrosaurus.admin.dashboard.service;

import io.github.lystrosaurus.admin.dashboard.vo.DashboardVO;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeDAO;
import io.github.lystrosaurus.admin.organization.orgunit.dao.OrgUnitDAO;
import io.github.lystrosaurus.admin.system.role.dao.RoleDAO;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 仪表盘服务实现 */
@Service
@RequiredArgsConstructor
public class DashboardService {

  private final UserQueryRepository userQueryRepository;
  private final RoleDAO roleDAO;
  private final EmployeeDAO employeeDAO;
  private final OrgUnitDAO orgUnitDAO;

  public DashboardVO getStats() {
    long userCount = userQueryRepository.countByCondition(null, null);
    long roleCount = roleDAO.countByCondition(null, null);
    long employeeCount = employeeDAO.countByCondition(null, null, null);
    long orgUnitCount = orgUnitDAO.countByCondition(null, null, null);
    return new DashboardVO(userCount, roleCount, employeeCount, orgUnitCount);
  }
}

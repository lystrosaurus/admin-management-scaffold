package io.github.lystrosaurus.admin.dashboard.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.dashboard.vo.DashboardVO;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeDAO;
import io.github.lystrosaurus.admin.organization.orgunit.dao.OrgUnitDAO;
import io.github.lystrosaurus.admin.system.role.dao.RoleDAO;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** DashboardService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("仪表盘服务测试")
class DashboardServiceTest {

  @Mock private UserDAO userDAO;

  @Mock private RoleDAO roleDAO;

  @Mock private EmployeeDAO employeeDAO;

  @Mock private OrgUnitDAO orgUnitDAO;

  @InjectMocks private DashboardService dashboardService;

  @Test
  @DisplayName("应该成功获取统计数据")
  void should_get_stats() {
    when(userDAO.countByCondition(null, null)).thenReturn(10L);
    when(roleDAO.countByCondition(null, null)).thenReturn(5L);
    when(employeeDAO.countByCondition(null, null, null)).thenReturn(100L);
    when(orgUnitDAO.countByCondition(null, null, null)).thenReturn(20L);

    DashboardVO result = dashboardService.getStats();

    assertNotNull(result);
    assertEquals(10L, result.userCount());
    assertEquals(5L, result.roleCount());
    assertEquals(100L, result.employeeCount());
    assertEquals(20L, result.orgUnitCount());
    verify(userDAO).countByCondition(null, null);
    verify(roleDAO).countByCondition(null, null);
    verify(employeeDAO).countByCondition(null, null, null);
    verify(orgUnitDAO).countByCondition(null, null, null);
  }
}

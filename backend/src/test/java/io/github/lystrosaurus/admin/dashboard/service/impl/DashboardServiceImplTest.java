package io.github.lystrosaurus.admin.dashboard.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

/** DashboardServiceImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("仪表盘服务测试")
class DashboardServiceImplTest {

  @Mock private UserDAO userDAO;

  @Mock private RoleDAO roleDAO;

  @Mock private EmployeeDAO employeeDAO;

  @Mock private OrgUnitDAO orgUnitDAO;

  @InjectMocks private DashboardServiceImpl dashboardService;

  @Test
  @DisplayName("应该成功获取统计数据")
  void should_get_stats_successfully() {
    // Given
    when(userDAO.countByCondition(null, null)).thenReturn(128L);
    when(roleDAO.countByCondition(null, null)).thenReturn(12L);
    when(employeeDAO.countByCondition(null, null, null)).thenReturn(96L);
    when(orgUnitDAO.countByCondition(null, null, null)).thenReturn(8L);

    // When
    DashboardVO result = dashboardService.getStats();

    // Then
    assertNotNull(result);
    assertEquals(128L, result.userCount());
    assertEquals(12L, result.roleCount());
    assertEquals(96L, result.employeeCount());
    assertEquals(8L, result.orgUnitCount());
    verify(userDAO).countByCondition(null, null);
    verify(roleDAO).countByCondition(null, null);
    verify(employeeDAO).countByCondition(null, null, null);
    verify(orgUnitDAO).countByCondition(null, null, null);
  }

  @Test
  @DisplayName("所有计数为零时应正确返回")
  void should_return_zero_counts_when_no_data() {
    // Given
    when(userDAO.countByCondition(null, null)).thenReturn(0L);
    when(roleDAO.countByCondition(null, null)).thenReturn(0L);
    when(employeeDAO.countByCondition(null, null, null)).thenReturn(0L);
    when(orgUnitDAO.countByCondition(null, null, null)).thenReturn(0L);

    // When
    DashboardVO result = dashboardService.getStats();

    // Then
    assertNotNull(result);
    assertEquals(0L, result.userCount());
    assertEquals(0L, result.roleCount());
    assertEquals(0L, result.employeeCount());
    assertEquals(0L, result.orgUnitCount());
  }
}

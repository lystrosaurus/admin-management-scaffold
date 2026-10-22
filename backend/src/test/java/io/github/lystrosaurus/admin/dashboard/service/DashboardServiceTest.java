package io.github.lystrosaurus.admin.dashboard.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.dashboard.vo.DashboardVO;
import io.github.lystrosaurus.admin.organization.employee.domain.acl.EmployeeQueryPort;
import io.github.lystrosaurus.admin.organization.orgunit.domain.acl.OrgUnitQueryPort;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleQueryRepository;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserQueryRepository;
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

  @Mock private UserQueryRepository userQueryRepository;

  @Mock private RoleQueryRepository roleQueryRepository;

  @Mock private EmployeeQueryPort employeeQueryPort;

  @Mock private OrgUnitQueryPort orgUnitQueryPort;

  @InjectMocks private DashboardService dashboardService;

  @Test
  @DisplayName("应该成功获取统计数据")
  void should_get_stats() {
    when(userQueryRepository.countByCondition(null, null)).thenReturn(10L);
    when(roleQueryRepository.countByCondition(null, null)).thenReturn(5L);
    when(employeeQueryPort.countAll()).thenReturn(100L);
    when(orgUnitQueryPort.countAll()).thenReturn(20L);

    DashboardVO result = dashboardService.getStats();

    assertNotNull(result);
    assertEquals(10L, result.userCount());
    assertEquals(5L, result.roleCount());
    assertEquals(100L, result.employeeCount());
    assertEquals(20L, result.orgUnitCount());
    verify(userQueryRepository).countByCondition(null, null);
    verify(roleQueryRepository).countByCondition(null, null);
    verify(employeeQueryPort).countAll();
    verify(orgUnitQueryPort).countAll();
  }
}

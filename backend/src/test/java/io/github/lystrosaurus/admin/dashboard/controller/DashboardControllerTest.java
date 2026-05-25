package io.github.lystrosaurus.admin.dashboard.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.lystrosaurus.admin.dashboard.service.DashboardService;
import io.github.lystrosaurus.admin.dashboard.vo.DashboardVO;
import io.github.lystrosaurus.admin.test.SaTokenTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * DashboardController 测试
 *
 * <p>测试仪表盘控制器的统计接口
 */
@DisplayName("DashboardController 测试")
class DashboardControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private DashboardService dashboardService;

  @Test
  @DisplayName("应该成功获取仪表盘统计数据")
  void should_get_dashboard_stats_successfully() throws Exception {
    // Given
    DashboardVO stats = new DashboardVO(128L, 12L, 96L, 8L);
    when(dashboardService.getStats()).thenReturn(stats);

    // When & Then
    mockMvc
        .perform(get("/app/dashboard/stats"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"))
        .andExpect(jsonPath("$.data.userCount").value(128))
        .andExpect(jsonPath("$.data.roleCount").value(12))
        .andExpect(jsonPath("$.data.employeeCount").value(96))
        .andExpect(jsonPath("$.data.orgUnitCount").value(8));
  }

  @Test
  @DisplayName("统计数据为零时应正确返回")
  void should_return_zero_counts_when_no_data() throws Exception {
    // Given
    DashboardVO stats = new DashboardVO(0L, 0L, 0L, 0L);
    when(dashboardService.getStats()).thenReturn(stats);

    // When & Then
    mockMvc
        .perform(get("/app/dashboard/stats"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.userCount").value(0))
        .andExpect(jsonPath("$.data.roleCount").value(0))
        .andExpect(jsonPath("$.data.employeeCount").value(0))
        .andExpect(jsonPath("$.data.orgUnitCount").value(0));
  }
}

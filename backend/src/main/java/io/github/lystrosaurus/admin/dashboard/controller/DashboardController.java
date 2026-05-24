package io.github.lystrosaurus.admin.dashboard.controller;

import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.dashboard.service.DashboardService;
import io.github.lystrosaurus.admin.dashboard.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘控制器
 *
 * <p>提供系统统计数据查询接口
 */
@RestController
@RequestMapping("/app/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  /**
   * 获取系统统计数据
   *
   * @return 统计数据
   */
  @GetMapping("/stats")
  public ApiResponse<DashboardVO> getStats() {
    return ApiResponse.success(dashboardService.getStats());
  }
}

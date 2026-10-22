package io.github.lystrosaurus.admin.dashboard.adapter.web;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
import io.github.lystrosaurus.admin.dashboard.adapter.response.DashboardResponse;
import io.github.lystrosaurus.admin.dashboard.application.DashboardApplicationService;
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

  private final DashboardApplicationService dashboardApplicationService;

  /**
   * 获取系统统计数据
   *
   * @return 统计数据
   */
  @RequiresPermission("system:dashboard:view")
  @GetMapping("/stats")
  public ApiResponse<DashboardResponse> getStats() {
    return ApiResponse.success(dashboardApplicationService.getStats());
  }
}

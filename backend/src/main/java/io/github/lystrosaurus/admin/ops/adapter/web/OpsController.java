package io.github.lystrosaurus.admin.ops.adapter.web;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
import io.github.lystrosaurus.admin.ops.adapter.response.AppInfoResponse;
import io.github.lystrosaurus.admin.ops.application.OpsApplicationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 运维健康检查控制器 */
@RestController
@RequestMapping("/app/ops")
@RequiredArgsConstructor
public class OpsController {

  private final OpsApplicationService opsApplicationService;

  /** 健康检查 */
  @RequiresPermission("ops:view")
  @GetMapping("/health")
  public ApiResponse<Map<String, String>> health() {
    return ApiResponse.success(opsApplicationService.health());
  }

  /** 应用信息 */
  @RequiresPermission("ops:view")
  @GetMapping("/info")
  public ApiResponse<AppInfoResponse> info() {
    return ApiResponse.success(opsApplicationService.info());
  }
}

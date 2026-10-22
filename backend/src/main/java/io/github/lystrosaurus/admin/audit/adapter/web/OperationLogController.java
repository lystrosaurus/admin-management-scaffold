package io.github.lystrosaurus.admin.audit.adapter.web;

import io.github.lystrosaurus.admin.audit.adapter.request.OperationLogQueryRequest;
import io.github.lystrosaurus.admin.audit.adapter.response.OperationLogResponse;
import io.github.lystrosaurus.admin.audit.application.OperationLogApplicationService;
import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
import io.github.lystrosaurus.admin.shared.adapter.web.PageResult;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 操作日志控制器 */
@RestController
@RequestMapping("/app/audit/operation-logs")
@RequiredArgsConstructor
public class OperationLogController {

  private final OperationLogApplicationService operationLogApplicationService;

  /** 分页查询操作日志 */
  @RequiresPermission("audit:log:view")
  @GetMapping
  public ApiResponse<PageResult<OperationLogResponse>> list(
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String module,
      @RequestParam(required = false) String action,
      @RequestParam(required = false) LocalDateTime start,
      @RequestParam(required = false) LocalDateTime end,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    OperationLogQueryRequest request =
        new OperationLogQueryRequest(username, module, action, start, end, page, size);
    return ApiResponse.success(operationLogApplicationService.listOperationLogs(request));
  }
}

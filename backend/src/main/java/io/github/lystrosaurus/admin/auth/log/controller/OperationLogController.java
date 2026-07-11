package io.github.lystrosaurus.admin.auth.log.controller;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.auth.log.dto.OperationLogQueryDTO;
import io.github.lystrosaurus.admin.auth.log.service.OperationLogService;
import io.github.lystrosaurus.admin.auth.log.vo.OperationLogVO;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.common.PageResult;
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

  private final OperationLogService operationLogService;

  /** 分页查询操作日志 */
  @RequiresPermission("audit:log:view")
  @GetMapping
  public ApiResponse<PageResult<OperationLogVO>> list(
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String module,
      @RequestParam(required = false) String action,
      @RequestParam(required = false) LocalDateTime start,
      @RequestParam(required = false) LocalDateTime end,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    OperationLogQueryDTO dto =
        new OperationLogQueryDTO(username, module, action, start, end, page, size);
    return ApiResponse.success(operationLogService.listOperationLogs(dto));
  }
}

package io.github.lystrosaurus.admin.auth.log.controller;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.auth.log.dto.LoginLogQueryDTO;
import io.github.lystrosaurus.admin.auth.log.service.LoginLogService;
import io.github.lystrosaurus.admin.auth.log.vo.LoginLogVO;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.common.PageResult;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 登录日志控制器 */
@RestController
@RequestMapping("/app/audit/login-logs")
@RequiredArgsConstructor
public class LoginLogController {

  private final LoginLogService loginLogService;

  /** 分页查询登录日志 */
  @RequiresPermission("audit:log:view")
  @GetMapping
  public ApiResponse<PageResult<LoginLogVO>> list(
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String ipAddress,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) LocalDateTime start,
      @RequestParam(required = false) LocalDateTime end,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    LoginLogQueryDTO dto =
        new LoginLogQueryDTO(username, ipAddress, status, start, end, page, size);
    return ApiResponse.success(loginLogService.listLoginLogs(dto));
  }
}

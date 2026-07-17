package io.github.lystrosaurus.admin.ops.controller;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.ops.vo.AppInfoVO;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 运维健康检查控制器 */
@RestController
@RequestMapping("/app/ops")
@RequiredArgsConstructor
public class OpsController {

  private static final String VERSION = "1.0.0";

  private static final DateTimeFormatter START_TIME_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  @Value("${spring.application.name:admin-management-scaffold}")
  private String appName;

  private final Environment environment;

  /** 健康检查 */
  @RequiresPermission("ops:view")
  @GetMapping("/health")
  public ApiResponse<Map<String, String>> health() {
    return ApiResponse.success(Map.of("status", "UP"));
  }

  /** 应用信息 */
  @RequiresPermission("ops:view")
  @GetMapping("/info")
  public ApiResponse<AppInfoVO> info() {
    String profile =
        environment.getActiveProfiles().length == 0
            ? "default"
            : String.join(",", environment.getActiveProfiles());
    String startTime =
        LocalDateTime.ofInstant(
                Instant.ofEpochMilli(ManagementFactory.getRuntimeMXBean().getStartTime()),
                ZoneId.systemDefault())
            .format(START_TIME_FMT);
    return ApiResponse.success(
        new AppInfoVO(
            appName,
            VERSION,
            System.getProperty("java.version"),
            "UP",
            System.getProperty("os.name"),
            System.getProperty("os.arch"),
            profile,
            startTime));
  }
}

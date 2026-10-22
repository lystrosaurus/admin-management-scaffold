package io.github.lystrosaurus.admin.ops.application;

import io.github.lystrosaurus.admin.ops.adapter.response.AppInfoResponse;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/** 运维应用服务：组装健康状态与应用信息。 */
@Service
@RequiredArgsConstructor
public class OpsApplicationService {

  private static final String VERSION = "1.0.0";

  private static final DateTimeFormatter START_TIME_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  @Value("${spring.application.name:admin-management-scaffold}")
  private String appName;

  private final Environment environment;

  /** 健康检查 */
  public Map<String, String> health() {
    return Map.of("status", "UP");
  }

  /** 应用信息 */
  public AppInfoResponse info() {
    String profile =
        environment.getActiveProfiles().length == 0
            ? "default"
            : String.join(",", environment.getActiveProfiles());
    String startTime =
        LocalDateTime.ofInstant(
                Instant.ofEpochMilli(ManagementFactory.getRuntimeMXBean().getStartTime()),
                ZoneId.systemDefault())
            .format(START_TIME_FMT);
    return new AppInfoResponse(
        appName,
        VERSION,
        System.getProperty("java.version"),
        "UP",
        System.getProperty("os.name"),
        System.getProperty("os.arch"),
        profile,
        startTime);
  }
}

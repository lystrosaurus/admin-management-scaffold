package io.github.lystrosaurus.admin.integration.source.adapter.request;

import io.github.lystrosaurus.admin.integration.source.application.command.ExtSourceUpdateCommand;

/**
 * 外部身份源更新请求（字段与原 ExtSourceUpdateDTO 一致）。
 *
 * @param name 系统名称
 * @param sourceType 来源类型(HR/IM/OA)
 * @param tenantKey 租户标识
 * @param status 状态(ENABLED/DISABLED)
 * @param priority 优先级
 * @param configJson 扩展配置JSON
 */
public record ExtSourceUpdateRequest(
    String name,
    String sourceType,
    String tenantKey,
    String status,
    Integer priority,
    String configJson) {

  /** 转为应用层命令 */
  public ExtSourceUpdateCommand toCommand() {
    return new ExtSourceUpdateCommand(name, sourceType, tenantKey, status, priority, configJson);
  }
}

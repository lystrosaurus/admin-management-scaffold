package io.github.lystrosaurus.admin.integration.source.adapter.request;

import io.github.lystrosaurus.admin.integration.source.application.command.ExtSourceCreateCommand;

/**
 * 外部身份源创建请求（字段与原 ExtSourceCreateDTO 一致）。
 *
 * @param code 系统编码
 * @param name 系统名称
 * @param sourceType 来源类型(HR/IM/OA)
 * @param tenantKey 租户标识
 * @param priority 优先级
 * @param configJson 扩展配置JSON
 */
public record ExtSourceCreateRequest(
    String code,
    String name,
    String sourceType,
    String tenantKey,
    Integer priority,
    String configJson) {

  /** 转为应用层命令 */
  public ExtSourceCreateCommand toCommand() {
    return new ExtSourceCreateCommand(code, name, sourceType, tenantKey, priority, configJson);
  }
}

package io.github.lystrosaurus.admin.system.configitem.adapter.request;

import io.github.lystrosaurus.admin.system.configitem.application.command.ConfigItemCreateCommand;
import jakarta.validation.constraints.NotBlank;

/**
 * 配置项创建请求（字段与原 ConfigItemCreateDTO 一致）。
 *
 * @param configKey 配置键
 * @param configValue 配置值
 * @param configType 配置类型(STRING/NUMBER/BOOLEAN/JSON)
 * @param description 描述
 * @param status 状态
 */
public record ConfigItemCreateRequest(
    @NotBlank(message = "配置键不能为空") String configKey,
    String configValue,
    String configType,
    String description,
    String status) {

  /** 转为应用层命令 */
  public ConfigItemCreateCommand toCommand() {
    return new ConfigItemCreateCommand(configKey, configValue, configType, description, status);
  }
}

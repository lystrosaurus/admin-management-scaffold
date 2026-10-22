package io.github.lystrosaurus.admin.system.configitem.adapter.request;

import io.github.lystrosaurus.admin.system.configitem.application.command.ConfigItemUpdateCommand;

/**
 * 配置项更新请求（字段与原 ConfigItemUpdateDTO 一致）。
 *
 * @param configKey 配置键
 * @param configValue 配置值
 * @param configType 配置类型(STRING/NUMBER/BOOLEAN/JSON)
 * @param description 描述
 * @param status 状态
 */
public record ConfigItemUpdateRequest(
    String configKey, String configValue, String configType, String description, String status) {

  /** 转为应用层命令 */
  public ConfigItemUpdateCommand toCommand() {
    return new ConfigItemUpdateCommand(configKey, configValue, configType, description, status);
  }
}

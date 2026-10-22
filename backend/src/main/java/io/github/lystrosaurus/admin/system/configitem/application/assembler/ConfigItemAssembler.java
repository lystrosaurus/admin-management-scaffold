package io.github.lystrosaurus.admin.system.configitem.application.assembler;

import io.github.lystrosaurus.admin.system.configitem.adapter.response.ConfigItemResponse;
import io.github.lystrosaurus.admin.system.configitem.domain.model.ConfigItem;
import org.springframework.stereotype.Component;

/** 配置项装配器：聚合 → 响应模型。 */
@Component
public class ConfigItemAssembler {

  /** 聚合转配置项响应 */
  public ConfigItemResponse toResponse(ConfigItem configItem) {
    return new ConfigItemResponse(
        configItem.id().value(),
        configItem.configKey(),
        configItem.configValue(),
        configItem.configType(),
        configItem.description(),
        configItem.status());
  }
}

package io.github.lystrosaurus.admin.system.configitem.infrastructure.converter;

import io.github.lystrosaurus.admin.system.configitem.domain.model.ConfigItem;
import io.github.lystrosaurus.admin.system.configitem.domain.model.ConfigItemId;
import io.github.lystrosaurus.admin.system.configitem.infrastructure.persistence.po.ConfigItemPO;
import org.springframework.stereotype.Component;

/** 配置项聚合 ↔ 持久化对象转换器。审计字段由 MyBatis-Plus 自动填充，不在此转换。 */
@Component
public class ConfigItemPoConverter {

  /** 领域对象转持久化对象 */
  public ConfigItemPO toPo(ConfigItem configItem) {
    ConfigItemPO po = new ConfigItemPO();
    po.setId(configItem.id() == null ? null : configItem.id().value());
    po.setConfigKey(configItem.configKey());
    po.setConfigValue(configItem.configValue());
    po.setConfigType(configItem.configType());
    po.setDescription(configItem.description());
    po.setStatus(configItem.status());
    po.setVersion(configItem.version());
    return po;
  }

  /** 持久化对象转领域对象（重建聚合，不登记事件） */
  public ConfigItem toDomain(ConfigItemPO po) {
    return ConfigItem.restore(
        ConfigItemId.of(po.getId()),
        po.getConfigKey(),
        po.getConfigValue(),
        po.getConfigType(),
        po.getDescription(),
        po.getStatus(),
        po.getCreatedAt(),
        po.getVersion());
  }
}

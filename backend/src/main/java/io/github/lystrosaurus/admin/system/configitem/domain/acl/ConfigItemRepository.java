package io.github.lystrosaurus.admin.system.configitem.domain.acl;

import io.github.lystrosaurus.admin.shared.domain.acl.GenericRepository;
import io.github.lystrosaurus.admin.system.configitem.domain.model.ConfigItem;
import io.github.lystrosaurus.admin.system.configitem.domain.model.ConfigItemId;

/** 配置项仓储端口：配置项聚合的存取契约。 */
public interface ConfigItemRepository extends GenericRepository<ConfigItem, ConfigItemId> {

  /** 判断配置键是否存在 */
  boolean existsByConfigKey(String configKey);

  /** 判断配置键是否存在且排除指定配置项 */
  boolean existsByConfigKeyAndIdNot(String configKey, ConfigItemId id);
}

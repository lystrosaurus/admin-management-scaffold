package io.github.lystrosaurus.admin.system.configitem.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.GenericRepositoryImpl;
import io.github.lystrosaurus.admin.system.configitem.domain.acl.ConfigItemRepository;
import io.github.lystrosaurus.admin.system.configitem.domain.model.ConfigItem;
import io.github.lystrosaurus.admin.system.configitem.domain.model.ConfigItemId;
import io.github.lystrosaurus.admin.system.configitem.infrastructure.converter.ConfigItemPoConverter;
import io.github.lystrosaurus.admin.system.configitem.infrastructure.persistence.mapper.ConfigItemMpMapper;
import io.github.lystrosaurus.admin.system.configitem.infrastructure.persistence.po.ConfigItemPO;
import org.springframework.stereotype.Repository;

/** 配置项仓储的 MyBatis-Plus 实现。 */
@Repository
public class ConfigItemRepositoryImpl
    extends GenericRepositoryImpl<ConfigItem, ConfigItemId, ConfigItemPO, ConfigItemMpMapper>
    implements ConfigItemRepository {

  private final ConfigItemPoConverter converter;

  public ConfigItemRepositoryImpl(
      ConfigItemMpMapper mapper,
      DomainEventPublisher eventPublisher,
      ConfigItemPoConverter converter) {
    super(mapper, eventPublisher);
    this.converter = converter;
  }

  @Override
  protected ConfigItemPO toPo(ConfigItem domain) {
    return converter.toPo(domain);
  }

  @Override
  protected ConfigItem toDomain(ConfigItemPO po) {
    return converter.toDomain(po);
  }

  @Override
  public boolean existsByConfigKey(String configKey) {
    return mapper.selectCount(
            new LambdaQueryWrapper<ConfigItemPO>().eq(ConfigItemPO::getConfigKey, configKey))
        > 0;
  }

  @Override
  public boolean existsByConfigKeyAndIdNot(String configKey, ConfigItemId id) {
    return mapper.selectCount(
            new LambdaQueryWrapper<ConfigItemPO>()
                .eq(ConfigItemPO::getConfigKey, configKey)
                .ne(ConfigItemPO::getId, id.value()))
        > 0;
  }
}

package io.github.lystrosaurus.admin.system.configitem.infrastructure.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.system.configitem.domain.acl.ConfigItemQueryRepository;
import io.github.lystrosaurus.admin.system.configitem.domain.model.ConfigItem;
import io.github.lystrosaurus.admin.system.configitem.infrastructure.converter.ConfigItemPoConverter;
import io.github.lystrosaurus.admin.system.configitem.infrastructure.persistence.mapper.ConfigItemMpMapper;
import io.github.lystrosaurus.admin.system.configitem.infrastructure.persistence.po.ConfigItemPO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/** 配置项查询端口的 MyBatis-Plus 实现（读模型侧）。 */
@Repository
@RequiredArgsConstructor
public class ConfigItemQueryRepositoryImpl implements ConfigItemQueryRepository {

  private final ConfigItemMpMapper configItemMpMapper;
  private final ConfigItemPoConverter converter;

  @Override
  public Page<ConfigItem> findPage(String configKey, PageQuery pageQuery) {
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<ConfigItemPO> mpPage =
        new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
            pageQuery.pageNum(), pageQuery.pageSize());
    LambdaQueryWrapper<ConfigItemPO> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.hasText(configKey)) {
      wrapper.like(ConfigItemPO::getConfigKey, configKey);
    }
    wrapper.orderByDesc(ConfigItemPO::getId);
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<ConfigItemPO> result =
        configItemMpMapper.selectPage(mpPage, wrapper);
    List<ConfigItem> records = result.getRecords().stream().map(converter::toDomain).toList();
    return new Page<>(records, result.getTotal(), pageQuery.pageNum(), pageQuery.pageSize());
  }
}

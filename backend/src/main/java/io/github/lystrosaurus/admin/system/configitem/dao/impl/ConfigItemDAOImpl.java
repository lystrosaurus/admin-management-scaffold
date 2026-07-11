package io.github.lystrosaurus.admin.system.configitem.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.system.configitem.dao.ConfigItemDAO;
import io.github.lystrosaurus.admin.system.configitem.entity.SysConfigItem;
import io.github.lystrosaurus.admin.system.configitem.mapper.SysConfigItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统配置项数据访问对象实现
 *
 * <p>使用 MyBatis-Plus Wrapper 构建查询条件。
 */
@Service
@RequiredArgsConstructor
public class ConfigItemDAOImpl implements ConfigItemDAO {

  private final SysConfigItemMapper configItemMapper;

  @Override
  public SysConfigItem findById(Long id) {
    return configItemMapper.selectById(id);
  }

  @Override
  public void save(SysConfigItem configItem) {
    configItemMapper.insert(configItem);
  }

  @Override
  public void update(SysConfigItem configItem) {
    configItemMapper.updateById(configItem);
  }

  @Override
  public void deleteById(Long id) {
    configItemMapper.deleteById(id);
  }

  @Override
  public Page<SysConfigItem> findPage(Page<SysConfigItem> page, LambdaQueryWrapper<SysConfigItem> wrapper) {
    return configItemMapper.selectPage(page, wrapper);
  }

  @Override
  public boolean existsByConfigKey(String configKey) {
    return configItemMapper.selectCount(
            new LambdaQueryWrapper<SysConfigItem>().eq(SysConfigItem::getConfigKey, configKey))
        > 0;
  }

  @Override
  public boolean existsByConfigKeyAndIdNot(String configKey, Long id) {
    return configItemMapper.selectCount(
            new LambdaQueryWrapper<SysConfigItem>()
                .eq(SysConfigItem::getConfigKey, configKey)
                .ne(SysConfigItem::getId, id))
        > 0;
  }
}

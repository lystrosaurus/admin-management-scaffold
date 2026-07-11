package io.github.lystrosaurus.admin.system.configitem.dao;

import io.github.lystrosaurus.admin.system.configitem.entity.SysConfigItem;
import java.util.List;

/**
 * 系统配置项数据访问对象接口
 *
 * <p>定义 Service 需要的数据访问语义，而不是简单地暴露 Mapper 方法。
 */
public interface ConfigItemDAO {

  /**
   * 根据ID查找配置项
   *
   * @param id 配置项ID
   * @return 配置项实体，不存在时返回null
   */
  SysConfigItem findById(Long id);

  /**
   * 保存配置项
   *
   * @param configItem 配置项实体
   */
  void save(SysConfigItem configItem);

  /**
   * 更新配置项
   *
   * @param configItem 配置项实体
   */
  void update(SysConfigItem configItem);

  /**
   * 根据ID逻辑删除配置项
   *
   * @param id 配置项ID
   */
  void deleteById(Long id);

  /**
   * 查询所有配置项
   *
   * @return 配置项列表
   */
  List<SysConfigItem> findAll();

  /**
   * 检查配置键是否已存在
   *
   * @param configKey 配置键
   * @return 存在返回true，否则返回false
   */
  boolean existsByConfigKey(String configKey);

  /**
   * 检查配置键是否已存在且排除指定ID
   *
   * @param configKey 配置键
   * @param id 排除的配置项ID
   * @return 存在返回true，否则返回false
   */
  boolean existsByConfigKeyAndIdNot(String configKey, Long id);
}

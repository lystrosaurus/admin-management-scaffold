package io.github.lystrosaurus.admin.system.configitem.mapstruct;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import io.github.lystrosaurus.admin.system.configitem.entity.SysConfigItem;
import io.github.lystrosaurus.admin.system.configitem.vo.ConfigItemVO;
import org.mapstruct.Mapper;

/** 系统配置项 MapStruct 映射器 */
@Mapper(componentModel = SPRING)
public interface ConfigItemMapper {

  /**
   * 将 SysConfigItem 转换为 ConfigItemVO
   *
   * @param sysConfigItem 配置项实体
   * @return 配置项VO
   */
  ConfigItemVO toConfigItemVO(SysConfigItem sysConfigItem);
}

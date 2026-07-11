package io.github.lystrosaurus.admin.system.dict.mapstruct;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import io.github.lystrosaurus.admin.system.dict.entity.SysDict;
import io.github.lystrosaurus.admin.system.dict.vo.DictVO;
import org.mapstruct.Mapper;

/** 字典 MapStruct 映射器 */
@Mapper(componentModel = SPRING)
public interface DictMapper {

  /**
   * 将 SysDict 转换为 DictVO
   *
   * @param sysDict 字典实体
   * @return 字典VO
   */
  DictVO toDictVO(SysDict sysDict);
}

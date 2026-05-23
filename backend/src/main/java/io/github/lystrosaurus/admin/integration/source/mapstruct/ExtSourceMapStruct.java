package io.github.lystrosaurus.admin.integration.source.mapstruct;

import io.github.lystrosaurus.admin.integration.source.dto.ExtSourceCreateDTO;
import io.github.lystrosaurus.admin.integration.source.dto.ExtSourceUpdateDTO;
import io.github.lystrosaurus.admin.integration.source.entity.ExtSource;
import io.github.lystrosaurus.admin.integration.source.vo.ExtSourceVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/** 外部身份源 MapStruct 映射器 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExtSourceMapStruct {

  /**
   * 将创建DTO转换为实体
   *
   * @param dto 创建DTO
   * @return 实体
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  ExtSource toEntity(ExtSourceCreateDTO dto);

  /**
   * 将实体转换为VO
   *
   * @param entity 实体
   * @return VO
   */
  ExtSourceVO toVO(ExtSource entity);

  /**
   * 将更新DTO合并到实体
   *
   * @param dto 更新DTO
   * @param entity 实体
   */
  void updateEntity(ExtSourceUpdateDTO dto, @MappingTarget ExtSource entity);
}

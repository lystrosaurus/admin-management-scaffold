package io.github.lystrosaurus.admin.integration.principal.mapstruct;

import io.github.lystrosaurus.admin.integration.principal.dto.ExtPrincipalCreateDTO;
import io.github.lystrosaurus.admin.integration.principal.dto.ExtPrincipalUpdateDTO;
import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipal;
import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipalIdentifier;
import io.github.lystrosaurus.admin.integration.principal.vo.ExtPrincipalIdentifierVO;
import io.github.lystrosaurus.admin.integration.principal.vo.ExtPrincipalVO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/** 外部主体 MapStruct 映射器 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExtPrincipalMapStruct {

  /**
   * 将创建DTO转换为实体
   *
   * @param dto 创建DTO
   * @return 实体
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "lastSyncAt", ignore = true)
  @Mapping(target = "canonicalType", ignore = true)
  @Mapping(target = "canonicalId", ignore = true)
  @Mapping(target = "linkStatus", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  ExtPrincipal toEntity(ExtPrincipalCreateDTO dto);

  /**
   * 将实体转换为VO
   *
   * @param entity 实体
   * @return VO
   */
  @Mapping(target = "sourceName", ignore = true)
  @Mapping(target = "identifiers", ignore = true)
  ExtPrincipalVO toVO(ExtPrincipal entity);

  /**
   * 将更新DTO合并到实体
   *
   * @param dto 更新DTO
   * @param entity 实体
   */
  void updateEntity(ExtPrincipalUpdateDTO dto, @MappingTarget ExtPrincipal entity);

  /**
   * 将标识符实体转换为VO
   *
   * @param entity 标识符实体
   * @return 标识符VO
   */
  @Mapping(target = "isPrimary", expression = "java(entity.getIsPrimary() == 1)")
  ExtPrincipalIdentifierVO toIdentifierVO(ExtPrincipalIdentifier entity);

  /**
   * 将标识符实体列表转换为VO列表
   *
   * @param entities 标识符实体列表
   * @return 标识符VO列表
   */
  List<ExtPrincipalIdentifierVO> toIdentifierVOList(List<ExtPrincipalIdentifier> entities);
}

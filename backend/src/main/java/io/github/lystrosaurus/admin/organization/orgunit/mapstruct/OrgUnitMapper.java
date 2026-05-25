package io.github.lystrosaurus.admin.organization.orgunit.mapstruct;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import io.github.lystrosaurus.admin.organization.orgunit.dto.OrgUnitCreateDTO;
import io.github.lystrosaurus.admin.organization.orgunit.entity.OrgUnit;
import io.github.lystrosaurus.admin.organization.orgunit.vo.OrgUnitTreeVO;
import io.github.lystrosaurus.admin.organization.orgunit.vo.OrgUnitVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** 组织单元 MapStruct 映射器 */
@Mapper(componentModel = SPRING)
public interface OrgUnitMapper {

  /**
   * 将 OrgUnitCreateDTO 转换为 OrgUnit 实体
   *
   * @param dto 组织单元创建DTO
   * @return 组织单元实体
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "fullPath", ignore = true)
  @Mapping(target = "level", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "sourceType", ignore = true)
  OrgUnit toEntity(OrgUnitCreateDTO dto);

  /**
   * 将 OrgUnit 实体转换为 OrgUnitVO
   *
   * @param entity 组织单元实体
   * @return 组织单元VO
   */
  OrgUnitVO toVO(OrgUnit entity);

  /**
   * 将 OrgUnit 实体转换为 OrgUnitTreeVO
   *
   * @param entity 组织单元实体
   * @return 组织单元树形VO
   */
  @Mapping(target = "children", ignore = true)
  OrgUnitTreeVO toTreeVO(OrgUnit entity);
}

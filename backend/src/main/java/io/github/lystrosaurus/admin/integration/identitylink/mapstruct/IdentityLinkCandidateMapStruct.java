package io.github.lystrosaurus.admin.integration.identitylink.mapstruct;

import io.github.lystrosaurus.admin.integration.identitylink.dto.IdentityLinkCandidateCreateDTO;
import io.github.lystrosaurus.admin.integration.identitylink.entity.IdentityLinkCandidate;
import io.github.lystrosaurus.admin.integration.identitylink.vo.IdentityLinkCandidateVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** 身份匹配候选 MapStruct 映射器 */
@Mapper(componentModel = "spring")
public interface IdentityLinkCandidateMapStruct {

  /**
   * 将 DTO 转换为实体
   *
   * @param dto 创建DTO
   * @return 实体
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "handledBy", ignore = true)
  @Mapping(target = "handledAt", ignore = true)
  IdentityLinkCandidate toEntity(IdentityLinkCandidateCreateDTO dto);

  /**
   * 将实体转换为 VO
   *
   * @param entity 实体
   * @return VO
   */
  IdentityLinkCandidateVO toVO(IdentityLinkCandidate entity);
}

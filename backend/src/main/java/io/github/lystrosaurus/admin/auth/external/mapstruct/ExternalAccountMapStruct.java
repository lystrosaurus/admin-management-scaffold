package io.github.lystrosaurus.admin.auth.external.mapstruct;

import io.github.lystrosaurus.admin.auth.external.dto.ExternalAccountBindDTO;
import io.github.lystrosaurus.admin.auth.external.entity.AuthExternalAccount;
import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** 三方账号 MapStruct 映射器 */
@Mapper(componentModel = "spring")
public interface ExternalAccountMapStruct {

  /**
   * 将绑定DTO转换为实体
   *
   * @param dto 绑定DTO
   * @return 实体
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "bindStatus", ignore = true)
  @Mapping(target = "lastLoginAt", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  AuthExternalAccount toEntity(ExternalAccountBindDTO dto);

  /**
   * 将实体转换为VO
   *
   * @param entity 实体
   * @return VO
   */
  @Mapping(target = "providerCode", ignore = true)
  ExternalAccountVO toVO(AuthExternalAccount entity);
}

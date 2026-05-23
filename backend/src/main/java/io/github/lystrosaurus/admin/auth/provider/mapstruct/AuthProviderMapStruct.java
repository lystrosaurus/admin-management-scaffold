package io.github.lystrosaurus.admin.auth.provider.mapstruct;

import io.github.lystrosaurus.admin.auth.provider.dto.AuthProviderCreateDTO;
import io.github.lystrosaurus.admin.auth.provider.dto.AuthProviderUpdateDTO;
import io.github.lystrosaurus.admin.auth.provider.entity.AuthProvider;
import io.github.lystrosaurus.admin.auth.provider.vo.AuthProviderVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/** 认证源 MapStruct 映射器 */
@Mapper(componentModel = "spring")
public interface AuthProviderMapStruct {

  /**
   * 将创建DTO转换为实体
   *
   * @param dto 创建DTO
   * @return 实体
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  AuthProvider toEntity(AuthProviderCreateDTO dto);

  /**
   * 将实体转换为VO
   *
   * <p>VO 不包含 clientSecretEncrypted，MapStruct 自动忽略源中存在但目标中不存在的属性。
   *
   * @param entity 实体
   * @return VO
   */
  AuthProviderVO toVO(AuthProvider entity);

  /**
   * 将更新DTO的非null字段更新到实体
   *
   * @param dto 更新DTO
   * @param entity 目标实体
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "code", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  void updateEntity(AuthProviderUpdateDTO dto, @MappingTarget AuthProvider entity);
}

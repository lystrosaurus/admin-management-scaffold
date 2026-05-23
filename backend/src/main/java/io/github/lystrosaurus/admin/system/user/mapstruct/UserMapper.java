package io.github.lystrosaurus.admin.system.user.mapstruct;

import io.github.lystrosaurus.admin.system.user.dto.UserCreateDTO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import io.github.lystrosaurus.admin.system.user.vo.UserDetailVO;
import io.github.lystrosaurus.admin.system.user.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/** 用户 MapStruct 映射器 */
@Mapper(componentModel = "spring")
public interface UserMapper {

  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  /**
   * 将 SysUser 转换为 UserVO
   *
   * @param sysUser 用户实体
   * @return 用户VO
   */
  @Mapping(target = "lastLoginAt", source = "lastLoginAt")
  @Mapping(target = "createdAt", source = "createdAt")
  UserVO toUserVO(SysUser sysUser);

  /**
   * 将 SysUser 转换为 UserDetailVO
   *
   * @param sysUser 用户实体
   * @return 用户详情VO
   */
  @Mapping(target = "lastLoginAt", source = "lastLoginAt")
  @Mapping(target = "lastLoginIp", source = "lastLoginIp")
  @Mapping(target = "createdAt", source = "createdAt")
  @Mapping(target = "roles", ignore = true) // 角色列表需要单独查询
  UserDetailVO toUserDetailVO(SysUser sysUser);

  /**
   * 将 UserCreateDTO 转换为 SysUser
   *
   * @param dto 用户创建DTO
   * @return 用户实体
   */
  @Mapping(target = "passwordHash", source = "password")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "avatarFileId", ignore = true)
  @Mapping(target = "employeeId", ignore = true)
  @Mapping(target = "tokenVersion", ignore = true)
  @Mapping(target = "lastLoginAt", ignore = true)
  @Mapping(target = "lastLoginIp", ignore = true)
  SysUser toEntity(UserCreateDTO dto);
}

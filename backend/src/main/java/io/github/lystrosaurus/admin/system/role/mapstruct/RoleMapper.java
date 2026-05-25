package io.github.lystrosaurus.admin.system.role.mapstruct;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import io.github.lystrosaurus.admin.system.role.dto.RoleCreateDTO;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import io.github.lystrosaurus.admin.system.role.vo.RoleDetailVO;
import io.github.lystrosaurus.admin.system.role.vo.RoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** 角色 MapStruct 映射器 */
@Mapper(componentModel = SPRING)
public interface RoleMapper {

  /**
   * 将 SysRole 转换为 RoleVO
   *
   * @param sysRole 角色实体
   * @return 角色VO
   */
  RoleVO toRoleVO(SysRole sysRole);

  /**
   * 将 SysRole 转换为 RoleDetailVO
   *
   * @param sysRole 角色实体
   * @return 角色详情VO
   */
  @Mapping(target = "permissions", ignore = true) // 权限列表需要单独查询
  @Mapping(target = "menus", ignore = true) // 菜单列表需要单独查询
  RoleDetailVO toRoleDetailVO(SysRole sysRole);

  /**
   * 将 RoleCreateDTO 转换为 SysRole 实体
   *
   * @param dto 角色创建DTO
   * @return 角色实体
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "version", ignore = true)
  SysRole toEntity(RoleCreateDTO dto);
}

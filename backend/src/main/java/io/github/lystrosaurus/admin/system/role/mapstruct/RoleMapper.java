package io.github.lystrosaurus.admin.system.role.mapstruct;

import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import io.github.lystrosaurus.admin.system.role.vo.RoleDetailVO;
import io.github.lystrosaurus.admin.system.role.vo.RoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/** 角色 MapStruct 映射器 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

  RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

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
  @Mapping(target = "createdAt", source = "createdAt")
  @Mapping(target = "permissions", ignore = true) // 权限列表需要单独查询
  @Mapping(target = "menus", ignore = true) // 菜单列表需要单独查询
  RoleDetailVO toRoleDetailVO(SysRole sysRole);
}

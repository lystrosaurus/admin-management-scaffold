package io.github.lystrosaurus.admin.system.permission.mapstruct;

import io.github.lystrosaurus.admin.system.permission.entity.SysPermission;
import io.github.lystrosaurus.admin.system.permission.vo.PermissionVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/** 权限 MapStruct 映射器 */
@Mapper(componentModel = "spring")
public interface PermissionMapper {

  PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

  /**
   * 将 SysPermission 转换为 PermissionVO
   *
   * @param sysPermission 权限实体
   * @return 权限VO
   */
  PermissionVO toPermissionVO(SysPermission sysPermission);
}

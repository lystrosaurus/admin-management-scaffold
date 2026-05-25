package io.github.lystrosaurus.admin.system.permission.mapstruct;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import io.github.lystrosaurus.admin.system.permission.entity.SysPermission;
import io.github.lystrosaurus.admin.system.permission.vo.PermissionVO;
import org.mapstruct.Mapper;

/** 权限 MapStruct 映射器 */
@Mapper(componentModel = SPRING)
public interface PermissionMapper {

  /**
   * 将 SysPermission 转换为 PermissionVO
   *
   * @param sysPermission 权限实体
   * @return 权限VO
   */
  PermissionVO toPermissionVO(SysPermission sysPermission);
}

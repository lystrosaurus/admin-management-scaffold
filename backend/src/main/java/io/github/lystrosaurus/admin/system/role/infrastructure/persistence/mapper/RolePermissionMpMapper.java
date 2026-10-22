package io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RolePermissionPO;
import org.apache.ibatis.annotations.Mapper;

/** 角色-权限关联 MyBatis-Plus Mapper。 */
@Mapper
public interface RolePermissionMpMapper extends BaseMapper<RolePermissionPO> {}

package io.github.lystrosaurus.admin.system.permission.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.system.permission.infrastructure.persistence.po.PermissionPO;
import org.apache.ibatis.annotations.Mapper;

/** 权限 MyBatis-Plus Mapper。 */
@Mapper
public interface PermissionMpMapper extends BaseMapper<PermissionPO> {}

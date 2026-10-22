package io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RolePO;
import org.apache.ibatis.annotations.Mapper;

/** 角色 MyBatis-Plus Mapper。 */
@Mapper
public interface RoleMpMapper extends BaseMapper<RolePO> {}

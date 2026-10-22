package io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.UserRolePO;
import org.apache.ibatis.annotations.Mapper;

/** 用户-角色关联 MyBatis-Plus Mapper。 */
@Mapper
public interface UserRoleMpMapper extends BaseMapper<UserRolePO> {}

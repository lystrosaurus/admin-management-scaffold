package io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RoleMenuPO;
import org.apache.ibatis.annotations.Mapper;

/** 角色-菜单关联 MyBatis-Plus Mapper。 */
@Mapper
public interface RoleMenuMpMapper extends BaseMapper<RoleMenuPO> {}

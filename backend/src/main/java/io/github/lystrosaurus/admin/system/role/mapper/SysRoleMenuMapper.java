package io.github.lystrosaurus.admin.system.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.system.role.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色菜单关联 Mapper
 *
 * <p>继承 MyBatis-Plus BaseMapper，提供基本的 CRUD 操作。
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {}

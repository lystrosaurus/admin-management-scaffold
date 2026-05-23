package io.github.lystrosaurus.admin.system.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统角色 Mapper
 *
 * <p>继承 MyBatis-Plus BaseMapper，提供基本的 CRUD 操作。
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {}

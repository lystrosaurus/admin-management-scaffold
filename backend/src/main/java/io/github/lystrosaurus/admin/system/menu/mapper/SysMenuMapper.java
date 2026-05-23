package io.github.lystrosaurus.admin.system.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.system.menu.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统菜单 Mapper
 *
 * <p>继承 MyBatis-Plus BaseMapper，提供基本的 CRUD 操作。
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {}

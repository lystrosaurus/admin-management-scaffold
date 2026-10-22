package io.github.lystrosaurus.admin.system.menu.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.system.menu.infrastructure.persistence.po.MenuPO;
import org.apache.ibatis.annotations.Mapper;

/** 菜单 MyBatis-Plus Mapper。 */
@Mapper
public interface MenuMpMapper extends BaseMapper<MenuPO> {}

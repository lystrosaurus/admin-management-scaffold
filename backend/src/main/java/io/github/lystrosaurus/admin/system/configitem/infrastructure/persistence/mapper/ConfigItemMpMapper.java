package io.github.lystrosaurus.admin.system.configitem.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.system.configitem.infrastructure.persistence.po.ConfigItemPO;
import org.apache.ibatis.annotations.Mapper;

/** 配置项 MyBatis-Plus Mapper。 */
@Mapper
public interface ConfigItemMpMapper extends BaseMapper<ConfigItemPO> {}

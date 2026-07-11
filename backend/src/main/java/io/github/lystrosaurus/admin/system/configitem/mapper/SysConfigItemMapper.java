package io.github.lystrosaurus.admin.system.configitem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.system.configitem.entity.SysConfigItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置项 Mapper
 *
 * <p>继承 MyBatis-Plus BaseMapper，提供基本的 CRUD 操作。
 */
@Mapper
public interface SysConfigItemMapper extends BaseMapper<SysConfigItem> {}

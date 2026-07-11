package io.github.lystrosaurus.admin.system.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.system.dict.entity.SysDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统字典 Mapper
 *
 * <p>继承 MyBatis-Plus BaseMapper，提供基本的 CRUD 操作。
 */
@Mapper
public interface SysDictMapper extends BaseMapper<SysDict> {}

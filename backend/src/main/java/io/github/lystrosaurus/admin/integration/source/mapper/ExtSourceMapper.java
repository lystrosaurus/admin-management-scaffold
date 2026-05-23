package io.github.lystrosaurus.admin.integration.source.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.integration.source.entity.ExtSource;
import org.apache.ibatis.annotations.Mapper;

/**
 * 外部身份源 Mapper
 *
 * <p>继承 MyBatis-Plus BaseMapper，提供基本的 CRUD 操作。
 */
@Mapper
public interface ExtSourceMapper extends BaseMapper<ExtSource> {}

package io.github.lystrosaurus.admin.integration.source.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.integration.source.infrastructure.persistence.po.ExtSourcePO;
import org.apache.ibatis.annotations.Mapper;

/** 外部身份源 MyBatis-Plus Mapper（仅允许 infrastructure 依赖）。 */
@Mapper
public interface ExtSourceMpMapper extends BaseMapper<ExtSourcePO> {}

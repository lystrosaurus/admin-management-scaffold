package io.github.lystrosaurus.admin.integration.principal.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.integration.principal.infrastructure.persistence.po.ExtPrincipalPO;
import org.apache.ibatis.annotations.Mapper;

/** 外部主体 MyBatis-Plus Mapper（仅允许 infrastructure 依赖）。 */
@Mapper
public interface ExtPrincipalMpMapper extends BaseMapper<ExtPrincipalPO> {}

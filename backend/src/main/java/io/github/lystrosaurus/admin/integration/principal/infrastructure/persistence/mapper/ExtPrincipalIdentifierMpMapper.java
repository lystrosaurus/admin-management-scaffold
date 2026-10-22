package io.github.lystrosaurus.admin.integration.principal.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.integration.principal.infrastructure.persistence.po.ExtPrincipalIdentifierPO;
import org.apache.ibatis.annotations.Mapper;

/** 外部主体标识符 MyBatis-Plus Mapper（仅允许 infrastructure 依赖）。 */
@Mapper
public interface ExtPrincipalIdentifierMpMapper extends BaseMapper<ExtPrincipalIdentifierPO> {}

package io.github.lystrosaurus.admin.integration.identitylink.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.integration.identitylink.infrastructure.persistence.po.IdentityLinkCandidatePO;
import org.apache.ibatis.annotations.Mapper;

/** 身份匹配候选 MyBatis-Plus Mapper（仅允许 infrastructure 依赖）。 */
@Mapper
public interface IdentityLinkCandidateMpMapper
    extends BaseMapper<IdentityLinkCandidatePO> {}

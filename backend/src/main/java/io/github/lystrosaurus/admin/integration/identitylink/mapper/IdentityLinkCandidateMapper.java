package io.github.lystrosaurus.admin.integration.identitylink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.integration.identitylink.entity.IdentityLinkCandidate;
import org.apache.ibatis.annotations.Mapper;

/** 身份匹配候选 Mapper */
@Mapper
public interface IdentityLinkCandidateMapper extends BaseMapper<IdentityLinkCandidate> {}

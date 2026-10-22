package io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence.po.OrgUnitPO;
import org.apache.ibatis.annotations.Mapper;

/** 组织单元 MyBatis-Plus Mapper（仅允许 infrastructure 依赖）。 */
@Mapper
public interface OrgUnitMpMapper extends BaseMapper<OrgUnitPO> {}

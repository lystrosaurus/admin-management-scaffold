package io.github.lystrosaurus.admin.organization.orgunit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.organization.orgunit.entity.OrgUnit;
import org.apache.ibatis.annotations.Mapper;

/**
 * 组织单元 Mapper
 *
 * <p>继承 MyBatis-Plus BaseMapper，提供基本的 CRUD 操作。
 */
@Mapper
public interface SysOrgUnitMapper extends BaseMapper<OrgUnit> {}

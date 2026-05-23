package io.github.lystrosaurus.admin.organization.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.organization.employee.entity.EmployeeOrg;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工-组织关联 Mapper
 *
 * <p>继承 MyBatis-Plus BaseMapper，提供基本的 CRUD 操作。
 */
@Mapper
public interface SysEmployeeOrgMapper extends BaseMapper<EmployeeOrg> {}

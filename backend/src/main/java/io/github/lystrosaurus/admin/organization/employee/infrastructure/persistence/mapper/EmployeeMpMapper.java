package io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.po.EmployeePO;
import org.apache.ibatis.annotations.Mapper;

/** 员工 MyBatis-Plus Mapper（仅允许 infrastructure 依赖）。 */
@Mapper
public interface EmployeeMpMapper extends BaseMapper<EmployeePO> {}

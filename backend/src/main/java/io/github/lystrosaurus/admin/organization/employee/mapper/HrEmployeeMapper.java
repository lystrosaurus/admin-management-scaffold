package io.github.lystrosaurus.admin.organization.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.organization.employee.entity.HrEmployee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工 Mapper
 *
 * <p>继承 MyBatis-Plus BaseMapper，提供基本的 CRUD 操作。
 */
@Mapper
public interface HrEmployeeMapper extends BaseMapper<HrEmployee> {}

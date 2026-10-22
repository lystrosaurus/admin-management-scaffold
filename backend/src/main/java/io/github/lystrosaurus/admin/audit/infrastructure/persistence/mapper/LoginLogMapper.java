package io.github.lystrosaurus.admin.audit.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.audit.infrastructure.persistence.po.LoginLogPO;
import org.apache.ibatis.annotations.Mapper;

/** 登录日志 MyBatis-Plus Mapper。 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLogPO> {}

package io.github.lystrosaurus.admin.audit.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.audit.infrastructure.persistence.po.OperationLogPO;
import org.apache.ibatis.annotations.Mapper;

/** 操作日志 MyBatis-Plus Mapper。 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLogPO> {}

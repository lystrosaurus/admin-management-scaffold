package io.github.lystrosaurus.admin.auth.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.auth.log.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/** 操作日志Mapper */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {}

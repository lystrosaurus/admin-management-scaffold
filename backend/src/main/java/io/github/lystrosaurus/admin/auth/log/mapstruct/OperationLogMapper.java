package io.github.lystrosaurus.admin.auth.log.mapstruct;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import io.github.lystrosaurus.admin.auth.log.entity.OperationLog;
import io.github.lystrosaurus.admin.auth.log.vo.OperationLogVO;
import org.mapstruct.Mapper;

/** 操作日志 MapStruct 映射器 */
@Mapper(componentModel = SPRING)
public interface OperationLogMapper {

  /** 将 OperationLog 转换为 OperationLogVO */
  OperationLogVO toVO(OperationLog operationLog);
}

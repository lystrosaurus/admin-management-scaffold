package io.github.lystrosaurus.admin.audit.infrastructure.converter;

import io.github.lystrosaurus.admin.audit.domain.model.OperationLog;
import io.github.lystrosaurus.admin.audit.infrastructure.persistence.po.OperationLogPO;
import org.springframework.stereotype.Component;

/** 操作日志领域对象 ↔ 持久化对象转换器。 */
@Component
public class OperationLogPoConverter {

  /** 领域对象转持久化对象（createdAt 由 MyBatis-Plus 自动填充） */
  public OperationLogPO toPo(OperationLog log) {
    OperationLogPO po = new OperationLogPO();
    po.setId(log.id());
    po.setUserId(log.userId());
    po.setOperationType(log.operationType());
    po.setTargetType(log.targetType());
    po.setTargetId(log.targetId());
    po.setDetailJson(log.detailJson());
    po.setIpAddress(log.ipAddress());
    return po;
  }

  /** 持久化对象转领域对象 */
  public OperationLog toDomain(OperationLogPO po) {
    return OperationLog.restore(
        po.getId(),
        po.getUserId(),
        po.getOperationType(),
        po.getTargetType(),
        po.getTargetId(),
        po.getDetailJson(),
        po.getIpAddress(),
        po.getCreatedAt());
  }
}

package io.github.lystrosaurus.admin.auth.log.service;

import io.github.lystrosaurus.admin.auth.log.dao.OperationLogDAO;
import io.github.lystrosaurus.admin.auth.log.entity.OperationLog;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 操作日志服务实现 */
@Service
@RequiredArgsConstructor
public class OperationLogService {

  private final OperationLogDAO operationLogDAO;

  public void recordOperation(
      Long userId,
      String operationType,
      String targetType,
      Long targetId,
      String detailJson,
      String ipAddress) {
    OperationLog log = new OperationLog();
    log.setUserId(userId);
    log.setOperationType(operationType);
    log.setTargetType(targetType);
    log.setTargetId(targetId);
    log.setDetailJson(detailJson);
    log.setIpAddress(ipAddress);
    log.setCreatedAt(LocalDateTime.now());
    operationLogDAO.save(log);
  }

  public List<OperationLog> getRecentOperations(Long userId, int limit) {
    return operationLogDAO.listByUserId(userId, limit);
  }
}

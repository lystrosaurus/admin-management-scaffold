package io.github.lystrosaurus.admin.auth.log.service.impl;

import io.github.lystrosaurus.admin.auth.log.dao.OperationLogDAO;
import io.github.lystrosaurus.admin.auth.log.entity.OperationLog;
import io.github.lystrosaurus.admin.auth.log.service.OperationLogService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 操作日志服务实现 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

  private final OperationLogDAO operationLogDAO;

  @Override
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

  @Override
  public List<OperationLog> getRecentOperations(Long userId, int limit) {
    return operationLogDAO.listByUserId(userId, limit);
  }
}

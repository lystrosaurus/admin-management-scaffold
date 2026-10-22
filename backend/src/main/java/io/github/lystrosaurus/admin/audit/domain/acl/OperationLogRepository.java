package io.github.lystrosaurus.admin.audit.domain.acl;

import io.github.lystrosaurus.admin.audit.domain.model.OperationLog;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import java.time.LocalDateTime;
import java.util.List;

/** 操作日志仓储端口。 */
public interface OperationLogRepository {

  /** 保存操作日志 */
  void save(OperationLog log);

  /** 查询用户最近的操作记录 */
  List<OperationLog> listByUserId(Long userId, int limit);

  /** 条件分页查询操作日志 */
  Page<OperationLog> findPage(
      Long userId,
      String targetType,
      String operationType,
      LocalDateTime start,
      LocalDateTime end,
      int page,
      int size);
}

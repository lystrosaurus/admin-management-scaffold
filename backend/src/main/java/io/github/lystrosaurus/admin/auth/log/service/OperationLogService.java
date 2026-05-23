package io.github.lystrosaurus.admin.auth.log.service;

import io.github.lystrosaurus.admin.auth.log.entity.OperationLog;
import java.util.List;

/** 操作日志服务接口 */
public interface OperationLogService {

  /**
   * 记录操作日志
   *
   * @param userId 用户ID
   * @param operationType 操作类型
   * @param targetType 目标类型
   * @param targetId 目标ID
   * @param detailJson 操作详情JSON
   * @param ipAddress IP地址
   */
  void recordOperation(
      Long userId,
      String operationType,
      String targetType,
      Long targetId,
      String detailJson,
      String ipAddress);

  /**
   * 查询用户最近的操作记录
   *
   * @param userId 用户ID
   * @param limit 限制数量
   * @return 操作日志列表
   */
  List<OperationLog> getRecentOperations(Long userId, int limit);
}

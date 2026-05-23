package io.github.lystrosaurus.admin.auth.log.dao;

import io.github.lystrosaurus.admin.auth.log.entity.OperationLog;
import java.util.List;

/** 操作日志数据访问对象接口 */
public interface OperationLogDAO {

  /**
   * 保存操作日志
   *
   * @param log 操作日志实体
   */
  void save(OperationLog log);

  /**
   * 查询用户最近的操作记录
   *
   * @param userId 用户ID
   * @param limit 限制数量
   * @return 操作日志列表
   */
  List<OperationLog> listByUserId(Long userId, int limit);
}

package io.github.lystrosaurus.admin.auth.log.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.auth.log.entity.OperationLog;
import java.time.LocalDateTime;
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

  /**
   * 条件分页查询操作日志
   *
   * @param userId 用户ID(可选)
   * @param targetType 目标类型/模块(可选)
   * @param operationType 操作类型/动作(可选)
   * @param start 起始时间(可选)
   * @param end 结束时间(可选)
   * @param page 页码
   * @param size 每页大小
   * @return 操作日志分页
   */
  Page<OperationLog> findPage(
      Long userId,
      String targetType,
      String operationType,
      LocalDateTime start,
      LocalDateTime end,
      int page,
      int size);
}

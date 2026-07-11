package io.github.lystrosaurus.admin.auth.log.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.auth.log.dao.OperationLogDAO;
import io.github.lystrosaurus.admin.auth.log.entity.OperationLog;
import io.github.lystrosaurus.admin.auth.log.mapper.OperationLogMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/** 操作日志数据访问对象实现 */
@Repository
@RequiredArgsConstructor
public class OperationLogDAOImpl implements OperationLogDAO {

  private final OperationLogMapper operationLogMapper;

  @Override
  public void save(OperationLog log) {
    operationLogMapper.insert(log);
  }

  @Override
  public List<OperationLog> listByUserId(Long userId, int limit) {
    return operationLogMapper.selectList(
        new LambdaQueryWrapper<OperationLog>()
            .eq(OperationLog::getUserId, userId)
            .orderByDesc(OperationLog::getCreatedAt)
            .last("LIMIT " + limit));
  }

  @Override
  public Page<OperationLog> findPage(
      Long userId,
      String targetType,
      String operationType,
      LocalDateTime start,
      LocalDateTime end,
      int page,
      int size) {
    LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
    if (userId != null) {
      wrapper.eq(OperationLog::getUserId, userId);
    }
    if (StringUtils.hasText(targetType)) {
      wrapper.eq(OperationLog::getTargetType, targetType);
    }
    if (StringUtils.hasText(operationType)) {
      wrapper.eq(OperationLog::getOperationType, operationType);
    }
    if (start != null) {
      wrapper.ge(OperationLog::getCreatedAt, start);
    }
    if (end != null) {
      wrapper.le(OperationLog::getCreatedAt, end);
    }
    wrapper.orderByDesc(OperationLog::getCreatedAt);
    return operationLogMapper.selectPage(new Page<>(page, size), wrapper);
  }
}

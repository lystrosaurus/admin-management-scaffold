package io.github.lystrosaurus.admin.audit.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.audit.domain.acl.OperationLogRepository;
import io.github.lystrosaurus.admin.audit.domain.model.OperationLog;
import io.github.lystrosaurus.admin.audit.infrastructure.converter.OperationLogPoConverter;
import io.github.lystrosaurus.admin.audit.infrastructure.persistence.mapper.OperationLogMapper;
import io.github.lystrosaurus.admin.audit.infrastructure.persistence.po.OperationLogPO;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/** 操作日志仓储的 MyBatis-Plus 实现。 */
@Repository
@RequiredArgsConstructor
public class OperationLogRepositoryImpl implements OperationLogRepository {

  private final OperationLogMapper operationLogMapper;
  private final OperationLogPoConverter converter;

  @Override
  public void save(OperationLog log) {
    operationLogMapper.insert(converter.toPo(log));
  }

  @Override
  public List<OperationLog> listByUserId(Long userId, int limit) {
    return operationLogMapper
        .selectList(
            new LambdaQueryWrapper<OperationLogPO>()
                .eq(OperationLogPO::getUserId, userId)
                .orderByDesc(OperationLogPO::getCreatedAt)
                .last("LIMIT " + limit))
        .stream()
        .map(converter::toDomain)
        .toList();
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
    LambdaQueryWrapper<OperationLogPO> wrapper = new LambdaQueryWrapper<>();
    if (userId != null) {
      wrapper.eq(OperationLogPO::getUserId, userId);
    }
    if (StringUtils.hasText(targetType)) {
      wrapper.eq(OperationLogPO::getTargetType, targetType);
    }
    if (StringUtils.hasText(operationType)) {
      wrapper.eq(OperationLogPO::getOperationType, operationType);
    }
    if (start != null) {
      wrapper.ge(OperationLogPO::getCreatedAt, start);
    }
    if (end != null) {
      wrapper.le(OperationLogPO::getCreatedAt, end);
    }
    wrapper.orderByDesc(OperationLogPO::getCreatedAt);
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<OperationLogPO> result =
        operationLogMapper.selectPage(
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size), wrapper);
    List<OperationLog> records = result.getRecords().stream().map(converter::toDomain).toList();
    return new Page<>(records, result.getTotal(), page, size);
  }
}

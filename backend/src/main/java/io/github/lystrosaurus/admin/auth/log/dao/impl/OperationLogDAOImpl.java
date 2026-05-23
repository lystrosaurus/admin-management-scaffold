package io.github.lystrosaurus.admin.auth.log.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.auth.log.dao.OperationLogDAO;
import io.github.lystrosaurus.admin.auth.log.entity.OperationLog;
import io.github.lystrosaurus.admin.auth.log.mapper.OperationLogMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}

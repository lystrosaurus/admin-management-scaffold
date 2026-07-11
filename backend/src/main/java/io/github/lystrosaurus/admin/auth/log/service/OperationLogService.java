package io.github.lystrosaurus.admin.auth.log.service;

import io.github.lystrosaurus.admin.auth.log.dao.OperationLogDAO;
import io.github.lystrosaurus.admin.auth.log.dto.OperationLogQueryDTO;
import io.github.lystrosaurus.admin.auth.log.entity.OperationLog;
import io.github.lystrosaurus.admin.auth.log.mapstruct.OperationLogMapper;
import io.github.lystrosaurus.admin.auth.log.vo.OperationLogVO;
import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** 操作日志服务实现 */
@Service
@RequiredArgsConstructor
public class OperationLogService {

  private final OperationLogDAO operationLogDAO;
  private final OperationLogMapper operationLogMapper;
  private final UserDAO userDAO;

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

  /** 条件分页查询操作日志 */
  public PageResult<OperationLogVO> listOperationLogs(OperationLogQueryDTO dto) {
    Long userId = resolveUserId(dto.username());
    if (StringUtils.hasText(dto.username()) && userId == null) {
      return new PageResult<>(List.of(), 0, dto.page(), dto.size());
    }
    var page =
        operationLogDAO.findPage(
            userId, dto.module(), dto.action(), dto.start(), dto.end(), dto.page(), dto.size());
    List<OperationLogVO> vos = page.getRecords().stream().map(operationLogMapper::toVO).toList();
    return new PageResult<>(vos, page.getTotal(), dto.page(), dto.size());
  }

  private Long resolveUserId(String username) {
    if (!StringUtils.hasText(username)) {
      return null;
    }
    var user = userDAO.findByUsername(username);
    return user == null ? null : user.getId();
  }
}

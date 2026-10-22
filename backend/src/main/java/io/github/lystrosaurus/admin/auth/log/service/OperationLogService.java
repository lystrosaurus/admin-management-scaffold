package io.github.lystrosaurus.admin.auth.log.service;

import io.github.lystrosaurus.admin.auth.log.dao.OperationLogDAO;
import io.github.lystrosaurus.admin.auth.log.dto.OperationLogQueryDTO;
import io.github.lystrosaurus.admin.auth.log.entity.OperationLog;
import io.github.lystrosaurus.admin.auth.log.vo.OperationLogVO;
import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** 操作日志服务实现 */
@Service
@RequiredArgsConstructor
public class OperationLogService {

  private final OperationLogDAO operationLogDAO;
  private final UserRepository userRepository;

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
    operationLogDAO.save(log);
  }

  public List<OperationLogVO> getRecentOperations(Long userId, int limit) {
    return operationLogDAO.listByUserId(userId, limit).stream().map(this::toVO).toList();
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
    List<OperationLogVO> vos = page.getRecords().stream().map(this::toVO).toList();
    return new PageResult<>(vos, page.getTotal(), dto.page(), dto.size());
  }

  private Long resolveUserId(String username) {
    if (!StringUtils.hasText(username)) {
      return null;
    }
    return userRepository.findByUsername(username).map(user -> user.id().value()).orElse(null);
  }

  private OperationLogVO toVO(OperationLog log) {
    return new OperationLogVO(
        log.getId(),
        resolveUsername(log.getUserId()),
        log.getTargetType(),
        log.getOperationType(),
        log.getTargetId(),
        log.getCreatedAt(),
        log.getIpAddress());
  }

  /** userId → username（用于日志展示），未知或解析失败返回 null */
  private String resolveUsername(Long userId) {
    if (userId == null) {
      return null;
    }
    return userRepository.findById(UserId.of(userId)).map(User::username).orElse(null);
  }
}

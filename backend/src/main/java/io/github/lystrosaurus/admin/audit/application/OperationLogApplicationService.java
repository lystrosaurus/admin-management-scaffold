package io.github.lystrosaurus.admin.audit.application;

import io.github.lystrosaurus.admin.audit.adapter.request.OperationLogQueryRequest;
import io.github.lystrosaurus.admin.audit.adapter.response.OperationLogResponse;
import io.github.lystrosaurus.admin.audit.domain.acl.OperationLogRepository;
import io.github.lystrosaurus.admin.audit.domain.model.OperationLog;
import io.github.lystrosaurus.admin.shared.adapter.web.PageResult;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** 操作日志应用服务：记录与查询操作日志。 */
@Service
@RequiredArgsConstructor
public class OperationLogApplicationService {

  private final OperationLogRepository operationLogRepository;
  private final UserRepository userRepository;

  /** 记录操作日志 */
  public void recordOperation(
      Long userId,
      String operationType,
      String targetType,
      Long targetId,
      String detailJson,
      String ipAddress) {
    OperationLog log =
        OperationLog.record(
            userId, operationType, targetType, targetId, detailJson, ipAddress);
    operationLogRepository.save(log);
  }

  /** 查询用户最近的操作记录 */
  public List<OperationLogResponse> getRecentOperations(Long userId, int limit) {
    return operationLogRepository.listByUserId(userId, limit).stream()
        .map(this::toResponse)
        .toList();
  }

  /** 条件分页查询操作日志 */
  public PageResult<OperationLogResponse> listOperationLogs(OperationLogQueryRequest request) {
    Long userId = resolveUserId(request.username());
    if (StringUtils.hasText(request.username()) && userId == null) {
      return new PageResult<>(List.of(), 0, request.page(), request.size());
    }
    Page<OperationLog> page =
        operationLogRepository.findPage(
            userId,
            request.module(),
            request.action(),
            request.start(),
            request.end(),
            request.page(),
            request.size());
    List<OperationLogResponse> responses = page.records().stream().map(this::toResponse).toList();
    return new PageResult<>(responses, page.total(), request.page(), request.size());
  }

  private Long resolveUserId(String username) {
    if (!StringUtils.hasText(username)) {
      return null;
    }
    return userRepository.findByUsername(username).map(user -> user.id().value()).orElse(null);
  }

  private OperationLogResponse toResponse(OperationLog log) {
    return new OperationLogResponse(
        log.id(),
        resolveUsername(log.userId()),
        log.targetType(),
        log.operationType(),
        log.targetId(),
        log.createdAt(),
        log.ipAddress());
  }

  /** userId → username（用于日志展示），未知或解析失败返回 null */
  private String resolveUsername(Long userId) {
    if (userId == null) {
      return null;
    }
    return userRepository.findById(UserId.of(userId)).map(User::username).orElse(null);
  }
}

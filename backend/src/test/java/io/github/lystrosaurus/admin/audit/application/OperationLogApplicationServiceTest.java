package io.github.lystrosaurus.admin.audit.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.audit.adapter.response.OperationLogResponse;
import io.github.lystrosaurus.admin.audit.domain.acl.OperationLogRepository;
import io.github.lystrosaurus.admin.audit.domain.model.OperationLog;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.domain.model.UserStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** OperationLogApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("操作日志应用服务测试")
class OperationLogApplicationServiceTest {

  @Mock private OperationLogRepository operationLogRepository;
  @Mock private UserRepository userRepository;

  @InjectMocks private OperationLogApplicationService operationLogApplicationService;

  @Test
  @DisplayName("应该成功记录操作日志")
  void should_record_operation_log() {
    operationLogApplicationService.recordOperation(
        1L, "CREATE", "User", 1L, "{}", "127.0.0.1");

    ArgumentCaptor<OperationLog> captor = ArgumentCaptor.forClass(OperationLog.class);
    verify(operationLogRepository).save(captor.capture());
    OperationLog saved = captor.getValue();
    assertEquals(1L, saved.userId());
    assertEquals("CREATE", saved.operationType());
    assertEquals("User", saved.targetType());
    assertEquals(1L, saved.targetId());
    assertEquals("{}", saved.detailJson());
    assertEquals("127.0.0.1", saved.ipAddress());
  }

  @Test
  @DisplayName("应该成功获取最近操作日志并解析用户名")
  void should_get_recent_operations() {
    OperationLog log =
        OperationLog.restore(
            1L,
            1L,
            "CREATE",
            "User",
            1L,
            "{}",
            "127.0.0.1",
            LocalDateTime.now());
    when(operationLogRepository.listByUserId(1L, 10)).thenReturn(List.of(log));

    User user =
        User.restore(
            UserId.of(1L), "admin", null, null, null, null, null, null, UserStatus.ENABLED, 1,
            null, null, null, 0);
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(user));

    List<OperationLogResponse> result = operationLogApplicationService.getRecentOperations(1L, 10);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("admin", result.get(0).username());
    assertEquals("User", result.get(0).module());
    assertEquals("CREATE", result.get(0).action());
    assertEquals(1L, result.get(0).bizId());
    assertEquals("127.0.0.1", result.get(0).ip());
    verify(operationLogRepository).listByUserId(1L, 10);
  }
}

package io.github.lystrosaurus.admin.auth.log.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.log.dao.OperationLogDAO;
import io.github.lystrosaurus.admin.auth.log.entity.OperationLog;
import io.github.lystrosaurus.admin.auth.log.vo.OperationLogVO;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.domain.model.UserStatus;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** OperationLogService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("操作日志服务测试")
class OperationLogServiceTest {

  @Mock private OperationLogDAO operationLogDAO;
  @Mock private UserRepository userRepository;

  @InjectMocks private OperationLogService operationLogService;

  @Test
  @DisplayName("应该成功记录操作日志")
  void should_record_operation_log() {
    operationLogService.recordOperation(1L, "CREATE", "User", 1L, "{}", "127.0.0.1");
    verify(operationLogDAO).save(any(OperationLog.class));
  }

  @Test
  @DisplayName("应该成功获取最近操作日志并解析用户名")
  void should_get_recent_operations() {
    OperationLog log = new OperationLog();
    log.setId(1L);
    log.setUserId(1L);
    log.setOperationType("CREATE");
    log.setTargetType("User");
    log.setTargetId(1L);
    log.setIpAddress("127.0.0.1");
    log.setCreatedAt(LocalDateTime.now());
    when(operationLogDAO.listByUserId(1L, 10)).thenReturn(Arrays.asList(log));

    User user =
        User.restore(
            UserId.of(1L), "admin", null, null, null, null, null, null, UserStatus.ENABLED, 1,
            null, null, null, 0);
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(user));

    List<OperationLogVO> result = operationLogService.getRecentOperations(1L, 10);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("admin", result.get(0).username());
    assertEquals("User", result.get(0).module());
    assertEquals("CREATE", result.get(0).action());
    assertEquals(1L, result.get(0).bizId());
    assertEquals("127.0.0.1", result.get(0).ip());
    verify(operationLogDAO).listByUserId(1L, 10);
  }
}

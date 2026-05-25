package io.github.lystrosaurus.admin.auth.log.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.log.dao.OperationLogDAO;
import io.github.lystrosaurus.admin.auth.log.entity.OperationLog;
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

  @InjectMocks private OperationLogService operationLogService;

  @Test
  @DisplayName("应该成功记录操作日志")
  void should_record_operation_log() {
    operationLogService.recordOperation(1L, "CREATE", "User", 1L, "{}", "127.0.0.1");
    verify(operationLogDAO).save(any(OperationLog.class));
  }

  @Test
  @DisplayName("应该成功获取最近操作日志")
  void should_get_recent_operations() {
    OperationLog log = new OperationLog();
    log.setId(1L);
    when(operationLogDAO.listByUserId(1L, 10)).thenReturn(Arrays.asList(log));

    List<OperationLog> result = operationLogService.getRecentOperations(1L, 10);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(operationLogDAO).listByUserId(1L, 10);
  }
}

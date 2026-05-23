package io.github.lystrosaurus.admin.auth.log.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.log.dao.OperationLogDAO;
import io.github.lystrosaurus.admin.auth.log.entity.OperationLog;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** OperationLogServiceImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OperationLogServiceImpl 测试")
class OperationLogServiceImplTest {

  @Mock private OperationLogDAO operationLogDAO;

  @InjectMocks private OperationLogServiceImpl operationLogService;

  @Test
  @DisplayName("应该成功记录操作日志")
  void should_record_operation_successfully() {
    // Given
    Long userId = 1L;
    String operationType = "BIND_EXTERNAL";
    String targetType = "EXTERNAL_ACCOUNT";
    Long targetId = 100L;
    String detailJson = "{\"provider\":\"LARK\"}";
    String ipAddress = "127.0.0.1";

    // When
    operationLogService.recordOperation(
        userId, operationType, targetType, targetId, detailJson, ipAddress);

    // Then
    ArgumentCaptor<OperationLog> captor = ArgumentCaptor.forClass(OperationLog.class);
    verify(operationLogDAO).save(captor.capture());
    OperationLog captured = captor.getValue();
    assertEquals(userId, captured.getUserId());
    assertEquals(operationType, captured.getOperationType());
    assertEquals(targetType, captured.getTargetType());
    assertEquals(targetId, captured.getTargetId());
    assertEquals(detailJson, captured.getDetailJson());
    assertEquals(ipAddress, captured.getIpAddress());
    assertNotNull(captured.getCreatedAt());
  }

  @Test
  @DisplayName("记录操作日志时detailJson可以为空")
  void should_record_operation_with_null_detail_json() {
    // Given
    Long userId = 1L;
    String operationType = "CHANGE_PASSWORD";
    String targetType = null;
    Long targetId = null;
    String detailJson = null;
    String ipAddress = "127.0.0.1";

    // When
    operationLogService.recordOperation(
        userId, operationType, targetType, targetId, detailJson, ipAddress);

    // Then
    ArgumentCaptor<OperationLog> captor = ArgumentCaptor.forClass(OperationLog.class);
    verify(operationLogDAO).save(captor.capture());
    OperationLog captured = captor.getValue();
    assertEquals(userId, captured.getUserId());
    assertEquals(operationType, captured.getOperationType());
    assertNull(captured.getTargetType());
    assertNull(captured.getTargetId());
    assertNull(captured.getDetailJson());
  }

  @Test
  @DisplayName("应该成功获取用户最近操作记录")
  void should_get_recent_operations() {
    // Given
    Long userId = 1L;
    int limit = 10;
    OperationLog log1 = new OperationLog();
    log1.setId(1L);
    log1.setUserId(userId);
    log1.setOperationType("BIND_EXTERNAL");
    log1.setCreatedAt(LocalDateTime.now().minusHours(2));

    OperationLog log2 = new OperationLog();
    log2.setId(2L);
    log2.setUserId(userId);
    log2.setOperationType("CHANGE_PASSWORD");
    log2.setCreatedAt(LocalDateTime.now());

    when(operationLogDAO.listByUserId(userId, limit)).thenReturn(Arrays.asList(log2, log1));

    // When
    List<OperationLog> result = operationLogService.getRecentOperations(userId, limit);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(2L, result.get(0).getId());
    assertEquals(1L, result.get(1).getId());
    verify(operationLogDAO).listByUserId(userId, limit);
  }
}

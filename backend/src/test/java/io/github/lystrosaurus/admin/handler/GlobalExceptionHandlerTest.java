package io.github.lystrosaurus.admin.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

/** GlobalExceptionHandler 单元测试 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  @InjectMocks private GlobalExceptionHandler globalExceptionHandler;

  @Test
  void should_return_api_response_when_handling_business_exception() {
    BusinessException exception = new BusinessException(ErrorCode.USER_NOT_FOUND);

    ApiResponse<Void> response = globalExceptionHandler.handleBusinessException(exception);

    assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), response.getCode());
    assertEquals(ErrorCode.USER_NOT_FOUND.getMessage(), response.getMessage());
    assertNull(response.getData());
  }

  @Test
  void should_return_api_response_when_handling_validation_exception() {
    MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
    org.springframework.validation.BindingResult bindingResult =
        mock(org.springframework.validation.BindingResult.class);
    FieldError fieldError = new FieldError("object", "field", "错误消息");

    when(exception.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

    ApiResponse<Void> response = globalExceptionHandler.handleValidationException(exception);

    assertEquals(ErrorCode.SYSTEM_400.getCode(), response.getCode());
    assertEquals("错误消息", response.getMessage());
    assertNull(response.getData());
  }

  @Test
  void should_return_api_response_when_handling_bind_exception() {
    BindException exception = mock(BindException.class);
    org.springframework.validation.BindingResult bindingResult =
        mock(org.springframework.validation.BindingResult.class);
    FieldError fieldError = new FieldError("object", "field", "绑定错误");

    when(exception.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

    ApiResponse<Void> response = globalExceptionHandler.handleBindException(exception);

    assertEquals(ErrorCode.SYSTEM_400.getCode(), response.getCode());
    assertEquals("绑定错误", response.getMessage());
    assertNull(response.getData());
  }

  @Test
  void should_return_api_response_when_handling_runtime_exception() {
    RuntimeException exception = new RuntimeException("运行时异常");

    ApiResponse<Void> response = globalExceptionHandler.handleRuntimeException(exception);

    assertEquals(ErrorCode.SYSTEM_500.getCode(), response.getCode());
    assertEquals(ErrorCode.SYSTEM_500.getMessage(), response.getMessage());
    assertNull(response.getData());
  }

  @Test
  void should_return_api_response_when_handling_general_exception() {
    Exception exception = new Exception("通用异常");

    ApiResponse<Void> response = globalExceptionHandler.handleException(exception);

    assertEquals(ErrorCode.SYSTEM_500.getCode(), response.getCode());
    assertEquals(ErrorCode.SYSTEM_500.getMessage(), response.getMessage());
    assertNull(response.getData());
  }
}

package io.github.lystrosaurus.admin.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** BusinessException 单元测试 */
class BusinessExceptionTest {

  @Test
  void should_create_business_exception_when_using_error_code() {
    BusinessException exception = new BusinessException(ErrorCode.USER_NOT_FOUND);

    assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
    assertEquals(ErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  void should_create_business_exception_when_using_code_and_message() {
    int code = 1001;
    String message = "自定义错误消息";
    BusinessException exception = new BusinessException(code, message);

    assertEquals(code, exception.getCode());
    assertEquals(message, exception.getMessage());
  }

  @Test
  void should_create_business_exception_when_using_message_only() {
    String message = "自定义错误消息";
    BusinessException exception = new BusinessException(message);

    assertEquals(ErrorCode.SYSTEM_500.getCode(), exception.getCode());
    assertEquals(message, exception.getMessage());
  }

  @Test
  void should_create_business_exception_with_cause_when_using_code_message_and_cause() {
    int code = 1001;
    String message = "自定义错误消息";
    Throwable cause = new RuntimeException("原始异常");
    BusinessException exception = new BusinessException(code, message, cause);

    assertEquals(code, exception.getCode());
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }
}

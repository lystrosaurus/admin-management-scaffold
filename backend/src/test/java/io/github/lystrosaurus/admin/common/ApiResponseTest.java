package io.github.lystrosaurus.admin.common;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.exception.ErrorCode;
import org.junit.jupiter.api.Test;

/** ApiResponse 单元测试 */
class ApiResponseTest {

  @Test
  void should_create_success_response_when_calling_success() {
    ApiResponse<String> response = ApiResponse.success();

    assertEquals(ErrorCode.SYSTEM_SUCCESS.getCode(), response.getCode());
    assertEquals(ErrorCode.SYSTEM_SUCCESS.getMessage(), response.getMessage());
    assertNull(response.getData());
    assertTrue(response.getTimestamp() > 0);
  }

  @Test
  void should_create_success_response_with_data_when_calling_success_with_data() {
    String testData = "test data";
    ApiResponse<String> response = ApiResponse.success(testData);

    assertEquals(ErrorCode.SYSTEM_SUCCESS.getCode(), response.getCode());
    assertEquals(ErrorCode.SYSTEM_SUCCESS.getMessage(), response.getMessage());
    assertEquals(testData, response.getData());
  }

  @Test
  void
      should_create_success_response_with_message_and_data_when_calling_success_with_message_and_data() {
    String message = "自定义消息";
    String testData = "test data";
    ApiResponse<String> response = ApiResponse.success(message, testData);

    assertEquals(ErrorCode.SYSTEM_SUCCESS.getCode(), response.getCode());
    assertEquals(message, response.getMessage());
    assertEquals(testData, response.getData());
  }

  @Test
  void should_create_error_response_when_calling_error_with_code_and_message() {
    int code = 400;
    String message = "请求参数错误";
    ApiResponse<Void> response = ApiResponse.error(code, message);

    assertEquals(code, response.getCode());
    assertEquals(message, response.getMessage());
    assertNull(response.getData());
  }

  @Test
  void should_create_error_response_when_calling_error_with_error_code() {
    ApiResponse<Void> response = ApiResponse.error(ErrorCode.SYSTEM_400);

    assertEquals(ErrorCode.SYSTEM_400.getCode(), response.getCode());
    assertEquals(ErrorCode.SYSTEM_400.getMessage(), response.getMessage());
    assertNull(response.getData());
  }
}

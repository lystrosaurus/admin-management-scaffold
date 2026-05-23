package io.github.lystrosaurus.admin.auth;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 登录失败测试
 *
 * <p>测试各种登录失败场景（纯单元测试，不需要 Spring 上下文）
 */
@DisplayName("登录失败测试")
class LoginFailureTest {

  @Test
  @DisplayName("应该能够处理用户不存在异常")
  void should_handle_user_not_found_exception() {
    // 测试用户不存在异常
    BusinessException exception = new BusinessException(ErrorCode.USER_NOT_FOUND);

    assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
    assertEquals(ErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("应该能够处理密码错误异常")
  void should_handle_password_mismatch_exception() {
    // 测试密码错误异常
    BusinessException exception = new BusinessException(ErrorCode.PASSWORD_MISMATCH);

    assertEquals(ErrorCode.PASSWORD_MISMATCH.getCode(), exception.getCode());
    assertEquals(ErrorCode.PASSWORD_MISMATCH.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("应该能够处理用户被禁用异常")
  void should_handle_user_disabled_exception() {
    // 测试用户被禁用异常
    BusinessException exception = new BusinessException(ErrorCode.USER_DISABLED);

    assertEquals(ErrorCode.USER_DISABLED.getCode(), exception.getCode());
    assertEquals(ErrorCode.USER_DISABLED.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("应该能够处理认证失败异常")
  void should_handle_auth_failure_exception() {
    // 测试认证失败异常
    BusinessException exception = new BusinessException(ErrorCode.AUTH_401);

    assertEquals(ErrorCode.AUTH_401.getCode(), exception.getCode());
    assertEquals(ErrorCode.AUTH_401.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("应该能够处理权限不足异常")
  void should_handle_permission_denied_exception() {
    // 测试权限不足异常
    BusinessException exception = new BusinessException(ErrorCode.AUTH_403);

    assertEquals(ErrorCode.AUTH_403.getCode(), exception.getCode());
    assertEquals(ErrorCode.AUTH_403.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("应该能够处理 Token 过期异常")
  void should_handle_token_expired_exception() {
    // 测试 Token 过期异常
    BusinessException exception = new BusinessException(ErrorCode.AUTH_TOKEN_EXPIRED);

    assertEquals(ErrorCode.AUTH_TOKEN_EXPIRED.getCode(), exception.getCode());
    assertEquals(ErrorCode.AUTH_TOKEN_EXPIRED.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("应该能够处理 Token 无效异常")
  void should_handle_token_invalid_exception() {
    // 测试 Token 无效异常
    BusinessException exception = new BusinessException(ErrorCode.AUTH_TOKEN_INVALID);

    assertEquals(ErrorCode.AUTH_TOKEN_INVALID.getCode(), exception.getCode());
    assertEquals(ErrorCode.AUTH_TOKEN_INVALID.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("应该能够处理用户已存在异常")
  void should_handle_user_already_exists_exception() {
    // 测试用户已存在异常
    BusinessException exception = new BusinessException(ErrorCode.USER_ALREADY_EXISTS);

    assertEquals(ErrorCode.USER_ALREADY_EXISTS.getCode(), exception.getCode());
    assertEquals(ErrorCode.USER_ALREADY_EXISTS.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("应该能够处理系统内部错误异常")
  void should_handle_system_internal_error_exception() {
    // 测试系统内部错误异常
    BusinessException exception = new BusinessException(ErrorCode.SYSTEM_500);

    assertEquals(ErrorCode.SYSTEM_500.getCode(), exception.getCode());
    assertEquals(ErrorCode.SYSTEM_500.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("应该能够处理自定义错误消息异常")
  void should_handle_custom_error_message_exception() {
    // 测试自定义错误消息异常
    BusinessException exception = new BusinessException("自定义错误消息");

    assertEquals(ErrorCode.SYSTEM_500.getCode(), exception.getCode());
    assertEquals("自定义错误消息", exception.getMessage());
  }
}

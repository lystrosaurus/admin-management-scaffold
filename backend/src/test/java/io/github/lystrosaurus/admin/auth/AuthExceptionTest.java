package io.github.lystrosaurus.admin.auth;

import static org.junit.jupiter.api.Assertions.*;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 认证异常测试
 *
 * <p>验证认证异常相关的类结构和错误码（纯单元测试，不依赖 SaTokenContext）
 */
@DisplayName("认证异常测试")
class AuthExceptionTest {

  @Test
  @DisplayName("NotLoginException 应该是 RuntimeException 的子类")
  void should_be_subtype_of_runtime_exception() {
    assertInstanceOf(RuntimeException.class, new NotLoginException("test", "test", "test"));
  }

  @Test
  @DisplayName("NotLoginException 应该有 TYPE 类型字段")
  void should_have_type_field() {
    NotLoginException exception = new NotLoginException("test", "test-token", "test-message");
    assertNotNull(exception.getType());
  }

  @Test
  @DisplayName("StpUtil 应该有 isLogin 方法")
  void should_have_isLogin_method() throws NoSuchMethodException {
    assertNotNull(StpUtil.class.getMethod("isLogin"));
  }

  @Test
  @DisplayName("StpUtil 应该有 getTokenValue 方法")
  void should_have_getTokenValue_method() throws NoSuchMethodException {
    assertNotNull(StpUtil.class.getMethod("getTokenValue"));
  }

  @Test
  @DisplayName("StpUtil 应该有 getLoginIdAsLong 方法")
  void should_have_getLoginIdAsLong_method() throws NoSuchMethodException {
    assertNotNull(StpUtil.class.getMethod("getLoginIdAsLong"));
  }

  @Test
  @DisplayName("BusinessException 应该有 AUTH_401 错误码")
  void should_have_auth_401_error_code() {
    BusinessException exception = new BusinessException(ErrorCode.AUTH_401);
    assertEquals(1001, exception.getCode());
    assertEquals("认证失败", exception.getMessage());
  }

  @Test
  @DisplayName("BusinessException 应该有 AUTH_403 错误码")
  void should_have_auth_403_error_code() {
    BusinessException exception = new BusinessException(ErrorCode.AUTH_403);
    assertEquals(1003, exception.getCode());
    assertEquals("权限不足", exception.getMessage());
  }
}

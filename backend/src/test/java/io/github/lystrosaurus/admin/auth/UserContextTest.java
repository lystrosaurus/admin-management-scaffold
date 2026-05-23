package io.github.lystrosaurus.admin.auth;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.auth.context.UserContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * UserContext 工具类测试
 *
 * <p>测试用户上下文工具类的各个方法（纯单元测试）
 */
@DisplayName("UserContext 工具类测试")
class UserContextTest {

  @Test
  @DisplayName("应该能够创建UserContext实例")
  void should_create_user_context_instance() {
    // UserContext是工具类，所有方法都是静态的
    // 验证类能够正常加载
    assertDoesNotThrow(
        () -> Class.forName("io.github.lystrosaurus.admin.auth.context.UserContext"));
  }

  @Test
  @DisplayName("应该有getCurrentUserId方法")
  void should_have_getCurrentUserId_method() throws NoSuchMethodException {
    // 验证方法存在
    assertNotNull(UserContext.class.getMethod("getCurrentUserId"));
  }

  @Test
  @DisplayName("应该有getCurrentUser方法")
  void should_have_getCurrentUser_method() throws NoSuchMethodException {
    // 验证方法存在
    assertNotNull(UserContext.class.getMethod("getCurrentUser"));
  }

  @Test
  @DisplayName("应该有isLoggedIn方法")
  void should_have_isLoggedIn_method() throws NoSuchMethodException {
    // 验证方法存在
    assertNotNull(UserContext.class.getMethod("isLoggedIn"));
  }

  @Test
  @DisplayName("应该有hasPermission方法")
  void should_have_hasPermission_method() throws NoSuchMethodException {
    // 验证方法存在
    assertNotNull(UserContext.class.getMethod("hasPermission", String.class));
  }

  @Test
  @DisplayName("应该有hasRole方法")
  void should_have_hasRole_method() throws NoSuchMethodException {
    // 验证方法存在
    assertNotNull(UserContext.class.getMethod("hasRole", String.class));
  }

  @Test
  @DisplayName("应该有getToken方法")
  void should_have_getToken_method() throws NoSuchMethodException {
    // 验证方法存在
    assertNotNull(UserContext.class.getMethod("getToken"));
  }

  @Test
  @DisplayName("应该有getPermissions方法")
  void should_have_getPermissions_method() throws NoSuchMethodException {
    // 验证方法存在
    assertNotNull(UserContext.class.getMethod("getPermissions"));
  }

  @Test
  @DisplayName("应该有getRoles方法")
  void should_have_getRoles_method() throws NoSuchMethodException {
    // 验证方法存在
    assertNotNull(UserContext.class.getMethod("getRoles"));
  }
}

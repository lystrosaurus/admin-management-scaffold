package io.github.lystrosaurus.admin.auth;

import static org.junit.jupiter.api.Assertions.*;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.auth.context.UserContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 权限检查测试
 *
 * <p>验证权限检查相关的类结构和方法（纯单元测试，不依赖 SaTokenContext）
 */
@DisplayName("权限检查测试")
class PermissionCheckTest {

  @Test
  @DisplayName("UserContext 应该有 hasPermission 方法")
  void should_have_hasPermission_method() throws NoSuchMethodException {
    assertNotNull(UserContext.class.getMethod("hasPermission", String.class));
  }

  @Test
  @DisplayName("UserContext 应该有 hasRole 方法")
  void should_have_hasRole_method() throws NoSuchMethodException {
    assertNotNull(UserContext.class.getMethod("hasRole", String.class));
  }

  @Test
  @DisplayName("UserContext 应该有 getPermissions 方法")
  void should_have_getPermissions_method() throws NoSuchMethodException {
    assertNotNull(UserContext.class.getMethod("getPermissions"));
  }

  @Test
  @DisplayName("UserContext 应该有 getRoles 方法")
  void should_have_getRoles_method() throws NoSuchMethodException {
    assertNotNull(UserContext.class.getMethod("getRoles"));
  }

  @Test
  @DisplayName("StpUtil 应该有 login 方法")
  void should_have_login_method() throws NoSuchMethodException {
    assertNotNull(StpUtil.class.getMethod("login", Object.class));
  }

  @Test
  @DisplayName("StpUtil 应该有 getPermissionList 方法")
  void should_have_getPermissionList_method() throws NoSuchMethodException {
    assertNotNull(StpUtil.class.getMethod("getPermissionList"));
  }

  @Test
  @DisplayName("StpUtil 应该有 getRoleList 方法")
  void should_have_getRoleList_method() throws NoSuchMethodException {
    assertNotNull(StpUtil.class.getMethod("getRoleList"));
  }
}

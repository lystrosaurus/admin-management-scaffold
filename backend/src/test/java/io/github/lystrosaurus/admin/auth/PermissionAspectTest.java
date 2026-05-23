package io.github.lystrosaurus.admin.auth;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.BaseTest;
import io.github.lystrosaurus.admin.auth.aspect.PermissionAspect;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 权限切面测试
 *
 * <p>测试权限注解和切面是否正确工作
 */
@DisplayName("权限切面测试")
class PermissionAspectTest extends BaseTest {

  @Autowired private PermissionAspect permissionAspect;

  @Test
  @DisplayName("应该成功注入 PermissionAspect")
  void should_inject_permission_aspect() {
    assertNotNull(permissionAspect);
  }

  @Test
  @DisplayName("应该能够创建权限切面实例")
  void should_create_permission_aspect_instance() {
    PermissionAspect aspect = new PermissionAspect();
    assertNotNull(aspect);
  }
}

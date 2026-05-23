package io.github.lystrosaurus.admin.auth;

import static org.junit.jupiter.api.Assertions.*;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import io.github.lystrosaurus.admin.auth.config.SaTokenConfig;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Sa-Token 配置测试
 *
 * <p>验证 Sa-Token 和 JWT 配置类的结构正确性（纯单元测试）
 */
@DisplayName("Sa-Token 配置测试")
class SaTokenConfigTest {

  @Test
  @DisplayName("应该能够创建 SaTokenConfig 实例")
  void should_create_sa_token_config_instance() {
    assertDoesNotThrow(
        () -> Class.forName("io.github.lystrosaurus.admin.auth.config.SaTokenConfig"));
  }

  @Test
  @DisplayName("应该有 getStpLogic 方法")
  void should_have_getStpLogic_method() throws NoSuchMethodException {
    Method method = SaTokenConfig.class.getMethod("getStpLogic");
    assertNotNull(method);
    assertEquals(StpLogic.class, method.getReturnType());
  }

  @Test
  @DisplayName("getStpLogic 应该返回 StpLogicJwtForSimple 实例")
  void should_return_stp_logic_jwt_for_simple() {
    SaTokenConfig config = new SaTokenConfig();
    StpLogic stpLogic = config.getStpLogic();
    assertNotNull(stpLogic);
    assertInstanceOf(StpLogicJwtForSimple.class, stpLogic);
  }

  @Test
  @DisplayName("应该有 @Configuration 注解")
  void should_have_configuration_annotation() {
    assertNotNull(
        SaTokenConfig.class.getAnnotation(
            org.springframework.context.annotation.Configuration.class));
  }
}

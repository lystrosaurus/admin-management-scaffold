package io.github.lystrosaurus.admin.auth;

import static org.junit.jupiter.api.Assertions.*;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import io.github.lystrosaurus.admin.auth.config.SaTokenConfig;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

  @Nested
  @DisplayName("JWT密钥校验测试")
  class JwtSecretValidationTest {

    @Test
    @DisplayName("空密钥应抛出异常")
    void should_throw_exception_for_empty_key() {
      SaTokenConfig config = new SaTokenConfig();
      setJwtSecretKey(config, "");

      IllegalStateException exception = assertThrows(IllegalStateException.class, config::validateJwtSecretKey);
      assertTrue(exception.getMessage().contains("must be configured"));
    }

    @Test
    @DisplayName("null密钥应抛出异常")
    void should_throw_exception_for_null_key() {
      SaTokenConfig config = new SaTokenConfig();
      setJwtSecretKey(config, null);

      IllegalStateException exception = assertThrows(IllegalStateException.class, config::validateJwtSecretKey);
      assertTrue(exception.getMessage().contains("must be configured"));
    }

    @Test
    @DisplayName("过短密钥应抛出异常")
    void should_throw_exception_for_short_key() {
      SaTokenConfig config = new SaTokenConfig();
      setJwtSecretKey(config, "only-20-chars-here!!!");

      IllegalStateException exception = assertThrows(IllegalStateException.class, config::validateJwtSecretKey);
      assertTrue(exception.getMessage().contains("at least 32 characters"));
    }

    @Test
  @DisplayName("默认示例密钥应抛出异常")
    void should_throw_exception_for_default_key() {
      SaTokenConfig config = new SaTokenConfig();
      setJwtSecretKey(config, "your-jwt-secret-key-at-least-32-chars-long");

      IllegalStateException exception = assertThrows(IllegalStateException.class, config::validateJwtSecretKey);
      assertTrue(exception.getMessage().contains("cannot use default/example value"));
    }

    @Test
    @DisplayName("有效密钥应通过校验")
    void should_pass_validation_for_valid_key() {
      SaTokenConfig config = new SaTokenConfig();
      setJwtSecretKey(config, "this-is-a-secure-jwt-secret-key-32chars!");

      assertDoesNotThrow(config::validateJwtSecretKey);
    }

    private void setJwtSecretKey(SaTokenConfig config, String key) {
      try {
        java.lang.reflect.Field field = SaTokenConfig.class.getDeclaredField("jwtSecretKey");
        field.setAccessible(true);
        field.set(config, key);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}

package io.github.lystrosaurus.admin.auth;

import static org.junit.jupiter.api.Assertions.*;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * JWT Token 测试
 *
 * <p>验证 JWT Token 相关的类和配置（纯单元测试，不依赖 SaTokenContext）
 */
@DisplayName("JWT Token 测试")
class JwtTokenTest {

  @Test
  @DisplayName("应该能够创建 StpLogicJwtForSimple 实例")
  void should_create_stp_logic_jwt_instance() {
    StpLogic stpLogic = new StpLogicJwtForSimple();
    assertNotNull(stpLogic);
  }

  @Test
  @DisplayName("StpLogicJwtForSimple 应该是 StpLogic 的子类")
  void should_be_subtype_of_stp_logic() {
    StpLogic stpLogic = new StpLogicJwtForSimple();
    assertInstanceOf(StpLogic.class, stpLogic);
  }

  @Test
  @DisplayName("应该能够加载 JWT 相关类")
  void should_load_jwt_classes() {
    assertDoesNotThrow(() -> Class.forName("cn.dev33.satoken.jwt.StpLogicJwtForSimple"));
  }

  @Test
  @DisplayName("StpLogicJwtForSimple 应该有 login 方法")
  void should_have_login_method() throws NoSuchMethodException {
    assertNotNull(StpLogicJwtForSimple.class.getMethod("login", Object.class));
  }

  @Test
  @DisplayName("StpLogicJwtForSimple 应该有 logout 方法")
  void should_have_logout_method() throws NoSuchMethodException {
    assertNotNull(StpLogicJwtForSimple.class.getMethod("logout"));
  }

  @Test
  @DisplayName("StpLogicJwtForSimple 应该有 isLogin 方法")
  void should_have_isLogin_method() throws NoSuchMethodException {
    assertNotNull(StpLogicJwtForSimple.class.getMethod("isLogin"));
  }

  @Test
  @DisplayName("StpLogicJwtForSimple 应该有 getTokenValue 方法")
  void should_have_getTokenValue_method() throws NoSuchMethodException {
    assertNotNull(StpLogicJwtForSimple.class.getMethod("getTokenValue"));
  }

  @Test
  @DisplayName("StpLogicJwtForSimple 应该有 getLoginId 方法")
  void should_have_getLoginId_method() throws NoSuchMethodException {
    assertNotNull(StpLogicJwtForSimple.class.getMethod("getLoginId"));
  }
}

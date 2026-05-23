package io.github.lystrosaurus.admin.auth;

import static org.junit.jupiter.api.Assertions.*;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * JWT 功能测试
 *
 * <p>验证 JWT 相关功能的类结构和方法（纯单元测试，不依赖 SaTokenContext）
 */
@DisplayName("JWT 功能测试")
class JwtFunctionTest {

  @Test
  @DisplayName("StpUtil 应该有 login 方法")
  void should_have_login_method() throws NoSuchMethodException {
    assertNotNull(StpUtil.class.getMethod("login", Object.class));
  }

  @Test
  @DisplayName("StpUtil 应该有 login 带超时方法")
  void should_have_login_with_timeout_method() throws NoSuchMethodException {
    assertNotNull(StpUtil.class.getMethod("login", Object.class, long.class));
  }

  @Test
  @DisplayName("StpUtil 应该有 logout 方法")
  void should_have_logout_method() throws NoSuchMethodException {
    assertNotNull(StpUtil.class.getMethod("logout"));
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
  @DisplayName("StpUtil 应该有 getTokenTimeout 方法")
  void should_have_getTokenTimeout_method() throws NoSuchMethodException {
    assertNotNull(StpUtil.class.getMethod("getTokenTimeout"));
  }

  @Test
  @DisplayName("StpUtil 应该有 renewTimeout 方法")
  void should_have_renewTimeout_method() throws NoSuchMethodException {
    assertNotNull(StpUtil.class.getMethod("renewTimeout", long.class));
  }

  @Test
  @DisplayName("StpUtil 应该有 getSession 方法")
  void should_have_getSession_method() throws NoSuchMethodException {
    assertNotNull(StpUtil.class.getMethod("getSession"));
  }

  @Test
  @DisplayName("StpUtil 应该有 getTokenInfo 方法")
  void should_have_getTokenInfo_method() throws NoSuchMethodException {
    assertNotNull(StpUtil.class.getMethod("getTokenInfo"));
  }

  @Test
  @DisplayName("StpLogicJwtForSimple 应该有 renewTimeout 方法")
  void should_have_renewTimeout_in_stp_logic() throws NoSuchMethodException {
    assertNotNull(StpLogicJwtForSimple.class.getMethod("renewTimeout", long.class));
  }

  @Test
  @DisplayName("StpLogic 应该有 getSession 方法")
  void should_have_getSession_in_stp_logic() throws NoSuchMethodException {
    assertNotNull(StpLogic.class.getMethod("getSession"));
  }
}

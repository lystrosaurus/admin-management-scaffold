package io.github.lystrosaurus.admin.auth;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

/**
 * Sa-Token 属性测试
 *
 * <p>测试 Sa-Token 配置属性是否正确加载
 */
@DisplayName("Sa-Token 属性测试")
class SaTokenPropertiesTest extends BaseTest {

  @Value("${sa-token.token-name:Authorization}")
  private String tokenName;

  @Value("${sa-token.token-prefix:Bearer}")
  private String tokenPrefix;

  @Value("${sa-token.timeout:7200}")
  private long timeout;

  @Value("${sa-token.active-timeout:-1}")
  private long activeTimeout;

  @Value("${sa-token.is-concurrent:true}")
  private boolean isConcurrent;

  @Value("${sa-token.is-share:false}")
  private boolean isShare;

  @Value("${sa-token.token-style:jwt}")
  private String tokenStyle;

  @Value("${sa-token.jwt-secret-key:your-jwt-secret-key-at-least-32-chars-long}")
  private String jwtSecretKey;

  @Value("${sa-token.is-read-header:true}")
  private boolean isReadHeader;

  @Value("${sa-token.is-read-cookie:false}")
  private boolean isReadCookie;

  @Value("${sa-token.is-read-body:false}")
  private boolean isReadBody;

  @Test
  @DisplayName("应该正确配置 token-name")
  void should_configure_token_name() {
    assertEquals("Authorization", tokenName);
  }

  @Test
  @DisplayName("应该正确配置 token-prefix")
  void should_configure_token_prefix() {
    assertEquals("Bearer", tokenPrefix);
  }

  @Test
  @DisplayName("应该正确配置 timeout")
  void should_configure_timeout() {
    assertEquals(7200, timeout);
  }

  @Test
  @DisplayName("应该正确配置 active-timeout")
  void should_configure_active_timeout() {
    assertEquals(-1, activeTimeout);
  }

  @Test
  @DisplayName("应该正确配置 is-concurrent")
  void should_configure_is_concurrent() {
    assertTrue(isConcurrent);
  }

  @Test
  @DisplayName("应该正确配置 is-share")
  void should_configure_is_share() {
    assertFalse(isShare);
  }

  @Test
  @DisplayName("应该正确配置 token-style")
  void should_configure_token_style() {
    assertEquals("jwt", tokenStyle);
  }

  @Test
  @DisplayName("应该正确配置 jwt-secret-key")
  void should_configure_jwt_secret_key() {
    assertNotNull(jwtSecretKey);
    assertTrue(jwtSecretKey.length() >= 32);
  }

  @Test
  @DisplayName("应该正确配置 is-read-header")
  void should_configure_is_read_header() {
    assertTrue(isReadHeader);
  }

  @Test
  @DisplayName("应该正确配置 is-read-cookie")
  void should_configure_is_read_cookie() {
    assertFalse(isReadCookie);
  }

  @Test
  @DisplayName("应该正确配置 is-read-body")
  void should_configure_is_read_body() {
    assertFalse(isReadBody);
  }
}

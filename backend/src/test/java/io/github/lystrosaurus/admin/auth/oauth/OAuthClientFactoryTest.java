package io.github.lystrosaurus.admin.auth.oauth;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthTokenResponse;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthUserInfo;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** OAuthClientFactory 单元测试 */
@DisplayName("OAuthClientFactory 测试")
class OAuthClientFactoryTest {

  private OAuthClientFactory factory;

  @BeforeEach
  void setUp() {
    Map<String, OAuthClient> clients = new HashMap<>();
    clients.put("LARK", new MockOAuthClient("LARK"));
    clients.put("WECOM", new MockOAuthClient("WECOM"));
    clients.put("WECHAT", new MockOAuthClient("WECHAT"));
    factory = new OAuthClientFactory(clients);
  }

  @Test
  @DisplayName("应该能获取 LARK Client")
  void should_get_lark_client() {
    // When
    OAuthClient client = factory.getClient("LARK");

    // Then
    assertNotNull(client);
    assertEquals("LARK", client.getProviderCode());
  }

  @Test
  @DisplayName("应该在未知 provider 时抛出 OAUTH_PROVIDER_NOT_FOUND")
  void should_throw_oauth_provider_not_found_when_unknown_provider() {
    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> factory.getClient("UNKNOWN"));
    assertEquals(ErrorCode.OAUTH_PROVIDER_NOT_FOUND.getCode(), exception.getCode());
  }

  /** 模拟 OAuthClient 实现 */
  private static class MockOAuthClient implements OAuthClient {
    private final String providerCode;

    MockOAuthClient(String providerCode) {
      this.providerCode = providerCode;
    }

    @Override
    public String getProviderCode() {
      return providerCode;
    }

    @Override
    public String buildAuthorizationUrl(String state, String redirectUri) {
      return "https://example.com/auth?state=" + state;
    }

    @Override
    public OAuthTokenResponse exchangeCode(String code, String redirectUri) {
      return new OAuthTokenResponse("token", null, 3600L, "Bearer");
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
      return new OAuthUserInfo("user123", "Test User", null, null);
    }
  }
}

package io.github.lystrosaurus.admin.auth.oauth.lark;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthTokenResponse;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthUserInfo;
import io.github.lystrosaurus.admin.auth.provider.service.AuthProviderService;
import io.github.lystrosaurus.admin.auth.provider.vo.AuthProviderVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/** LarkOAuthClient 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LarkOAuthClient 测试")
class LarkOAuthClientTest {

  @Mock private RestTemplate restTemplate;

  @Mock private AuthProviderService authProviderService;

  @InjectMocks private LarkOAuthClient larkOAuthClient;

  private AuthProviderVO providerVO;

  @BeforeEach
  void setUp() {
    providerVO =
        new AuthProviderVO(
            1L,
            "LARK",
            "飞书",
            "test-client-id",
            "https://example.com/callback",
            "openid",
            1,
            null,
            LocalDateTime.now(),
            LocalDateTime.now());
  }

  @Test
  @DisplayName("应该返回 LARK 作为 providerCode")
  void should_return_lark_as_provider_code() {
    assertEquals("LARK", larkOAuthClient.getProviderCode());
  }

  @Test
  @DisplayName("应该正确构建授权 URL")
  void should_build_authorization_url_correctly() {
    // Given
    when(authProviderService.getEnabledByCode("LARK")).thenReturn(providerVO);

    // When
    String url =
        larkOAuthClient.buildAuthorizationUrl("test-state", "https://example.com/callback");

    // Then
    assertNotNull(url);
    assertTrue(url.contains("app_id=test-client-id"));
    assertTrue(url.contains("state=test-state"));
    assertTrue(url.contains("scope=openid"));
  }

  @Test
  @DisplayName("应该成功换取 access_token")
  void should_exchange_code_successfully() {
    // Given - 模拟获取 app_access_token
    Map<String, Object> appTokenResponse = new HashMap<>();
    appTokenResponse.put("code", 0);
    appTokenResponse.put("app_access_token", "app-token-123");
    when(authProviderService.getEnabledByCode("LARK")).thenReturn(providerVO);
    when(authProviderService.getClientSecret("LARK")).thenReturn("test-client-secret");
    when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
        .thenReturn(appTokenResponse);

    // 模拟换取 user_access_token
    Map<String, Object> userTokenResponse = new HashMap<>();
    userTokenResponse.put("access_token", "user-token-456");
    userTokenResponse.put("refresh_token", "refresh-token-789");
    userTokenResponse.put("expires_in", 7200);
    userTokenResponse.put("token_type", "Bearer");
    ResponseEntity<Map> responseEntity = new ResponseEntity<>(userTokenResponse, HttpStatus.OK);
    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(responseEntity);

    // When
    OAuthTokenResponse result =
        larkOAuthClient.exchangeCode("auth-code", "https://example.com/callback");

    // Then
    assertNotNull(result);
    assertEquals("user-token-456", result.accessToken());
    assertEquals("refresh-token-789", result.refreshToken());
    assertEquals(7200L, result.expiresIn());
    assertEquals("Bearer", result.tokenType());
  }

  @Test
  @DisplayName("应该在换取 code 失败时抛出 OAUTH_CODE_EXCHANGE_FAILED")
  void should_throw_oauth_code_exchange_failed_when_exchange_fails() {
    // Given - 模拟获取 app_access_token 失败（返回错误码）
    Map<String, Object> appTokenResponse = new HashMap<>();
    appTokenResponse.put("code", 10014);
    appTokenResponse.put("msg", "invalid app_id");
    when(authProviderService.getEnabledByCode("LARK")).thenReturn(providerVO);
    when(authProviderService.getClientSecret("LARK")).thenReturn("test-client-secret");
    when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
        .thenReturn(appTokenResponse);

    // When & Then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> larkOAuthClient.exchangeCode("invalid-code", "https://example.com/callback"));
    assertEquals(ErrorCode.OAUTH_CODE_EXCHANGE_FAILED.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功获取用户信息")
  void should_get_user_info_successfully() {
    // Given
    Map<String, Object> userInfoResponse = new HashMap<>();
    userInfoResponse.put("open_id", "ou_xxx");
    userInfoResponse.put("name", "张三");
    userInfoResponse.put("avatar_url", "https://example.com/avatar.jpg");
    ResponseEntity<Map> responseEntity = new ResponseEntity<>(userInfoResponse, HttpStatus.OK);
    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(responseEntity);

    // When
    OAuthUserInfo result = larkOAuthClient.getUserInfo("user-access-token");

    // Then
    assertNotNull(result);
    assertEquals("ou_xxx", result.providerUserId());
    assertEquals("张三", result.nickname());
    assertEquals("https://example.com/avatar.jpg", result.avatarUrl());
  }

  @Test
  @DisplayName("应该在获取用户信息失败时抛出 OAUTH_USERINFO_FAILED")
  void should_throw_oauth_userinfo_failed_when_get_user_info_fails() {
    // Given
    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
        .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> larkOAuthClient.getUserInfo("invalid-token"));
    assertEquals(ErrorCode.OAUTH_USERINFO_FAILED.getCode(), exception.getCode());
  }
}

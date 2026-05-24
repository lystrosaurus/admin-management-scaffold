package io.github.lystrosaurus.admin.auth.oauth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.external.dao.AuthExternalAccountDAO;
import io.github.lystrosaurus.admin.auth.external.entity.AuthExternalAccount;
import io.github.lystrosaurus.admin.auth.external.service.ExternalAccountService;
import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import io.github.lystrosaurus.admin.auth.log.service.LoginLogService;
import io.github.lystrosaurus.admin.auth.oauth.OAuthClient;
import io.github.lystrosaurus.admin.auth.oauth.OAuthClientFactory;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthBindDTO;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthCallbackDTO;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthTokenResponse;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthUserInfo;
import io.github.lystrosaurus.admin.auth.oauth.service.impl.OAuthServiceImpl;
import io.github.lystrosaurus.admin.auth.oauth.state.OAuthStateService;
import io.github.lystrosaurus.admin.auth.oauth.state.StateData;
import io.github.lystrosaurus.admin.auth.oauth.vo.OAuthAuthorizeVO;
import io.github.lystrosaurus.admin.auth.oauth.vo.OAuthLoginVO;
import io.github.lystrosaurus.admin.auth.provider.service.AuthProviderService;
import io.github.lystrosaurus.admin.auth.provider.vo.AuthProviderVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/** OAuthServiceImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OAuthServiceImpl 测试")
class OAuthServiceImplTest {

  @Mock private OAuthClientFactory clientFactory;
  @Mock private OAuthStateService stateService;
  @Mock private AuthProviderService providerService;
  @Mock private ExternalAccountService externalAccountService;
  @Mock private AuthExternalAccountDAO accountDAO;
  @Mock private LoginLogService loginLogService;
  @Mock private OAuthClient oauthClient;

  @InjectMocks private OAuthServiceImpl oauthService;

  private AuthProviderVO providerVO;
  private OAuthTokenResponse tokenResponse;
  private OAuthUserInfo userInfo;
  private StateData stateData;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(oauthService, "redirectUri", "http://localhost:3000/oauth/callback");

    providerVO = new AuthProviderVO(
        10L, "LARK", "飞书", "app-id-123", "http://localhost:3000/oauth/callback",
        "openid", 1, null, LocalDateTime.now(), LocalDateTime.now());

    tokenResponse = new OAuthTokenResponse("access-token-123", "refresh-token", 7200L, "Bearer");
    userInfo = new OAuthUserInfo("open_id_123", "飞书用户", "https://avatar.url", "{\"open_id\":\"open_id_123\"}");
    stateData = new StateData("state-abc", "nonce-123", null, LocalDateTime.now());
  }

  @Test
  @DisplayName("authorize — 生成授权 URL")
  void should_generate_authorize_url() {
    when(providerService.getEnabledByCode("LARK")).thenReturn(providerVO);
    when(stateService.generateState()).thenReturn("state-abc");
    when(clientFactory.getClient("LARK")).thenReturn(oauthClient);
    when(oauthClient.buildAuthorizationUrl("state-abc", "http://localhost:3000/oauth/callback"))
        .thenReturn("https://open.feishu.cn/authorize?state=state-abc");

    OAuthAuthorizeVO result = oauthService.authorize("LARK");

    assertNotNull(result);
    assertEquals("state-abc", result.state());
    assertTrue(result.authorizeUrl().contains("state-abc"));
    verify(stateService).saveState("state-abc", null);
  }

  @Test
  @DisplayName("authorize — 提供方不存在时抛异常")
  void should_throw_when_provider_not_found_for_authorize() {
    when(providerService.getEnabledByCode("UNKNOWN"))
        .thenThrow(new BusinessException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND));

    assertThrows(BusinessException.class, () -> oauthService.authorize("UNKNOWN"));
  }

  @Test
  @DisplayName("handleCallback — 已绑定用户登录成功")
  void should_login_when_account_already_bound() {
    OAuthCallbackDTO callbackDTO = new OAuthCallbackDTO("auth-code", "state-abc");

    when(stateService.validateAndConsumeState("state-abc")).thenReturn(stateData);
    when(providerService.getEnabledByCode("LARK")).thenReturn(providerVO);
    when(clientFactory.getClient("LARK")).thenReturn(oauthClient);
    when(oauthClient.exchangeCode("auth-code", "http://localhost:3000/oauth/callback")).thenReturn(tokenResponse);
    when(oauthClient.getUserInfo("access-token-123")).thenReturn(userInfo);

    AuthExternalAccount existing = new AuthExternalAccount();
    existing.setId(1L);
    existing.setUserId(100L);
    when(accountDAO.findByProviderIdAndProviderUserId(10L, "open_id_123")).thenReturn(existing);

    OAuthLoginVO result = oauthService.handleCallback("LARK", callbackDTO);

    assertNotNull(result);
    assertNull(result.accessToken());
    assertFalse(result.needBind());
    verify(accountDAO).updateById(existing);
    verify(loginLogService).recordLogin(eq(100L), eq("OAUTH"), eq("LARK"), isNull(), isNull(), eq(true), isNull());
  }

  @Test
  @DisplayName("handleCallback — 未绑定用户返回 needBind")
  void should_return_need_bind_when_account_not_bound() {
    OAuthCallbackDTO callbackDTO = new OAuthCallbackDTO("auth-code", "state-abc");

    when(stateService.validateAndConsumeState("state-abc")).thenReturn(stateData);
    when(providerService.getEnabledByCode("LARK")).thenReturn(providerVO);
    when(clientFactory.getClient("LARK")).thenReturn(oauthClient);
    when(oauthClient.exchangeCode("auth-code", "http://localhost:3000/oauth/callback")).thenReturn(tokenResponse);
    when(oauthClient.getUserInfo("access-token-123")).thenReturn(userInfo);
    when(accountDAO.findByProviderIdAndProviderUserId(10L, "open_id_123")).thenReturn(null);

    OAuthLoginVO result = oauthService.handleCallback("LARK", callbackDTO);

    assertNotNull(result);
    assertTrue(result.needBind());
    assertNotNull(result.externalUserIdentity());
  }

  @Test
  @DisplayName("handleCallback — state 无效时抛异常")
  void should_throw_when_state_invalid() {
    OAuthCallbackDTO callbackDTO = new OAuthCallbackDTO("auth-code", "invalid-state");

    when(stateService.validateAndConsumeState("invalid-state"))
        .thenThrow(new BusinessException(ErrorCode.OAUTH_STATE_INVALID));

    assertThrows(BusinessException.class, () -> oauthService.handleCallback("LARK", callbackDTO));
  }

  @Test
  @DisplayName("bindAccount — 绑定成功")
  void should_bind_account_successfully() {
    OAuthBindDTO bindDTO = new OAuthBindDTO("auth-code", "state-abc");

    when(stateService.validateAndConsumeState("state-abc")).thenReturn(stateData);
    when(providerService.getEnabledByCode("LARK")).thenReturn(providerVO);
    when(clientFactory.getClient("LARK")).thenReturn(oauthClient);
    when(oauthClient.exchangeCode("auth-code", "http://localhost:3000/oauth/callback")).thenReturn(tokenResponse);
    when(oauthClient.getUserInfo("access-token-123")).thenReturn(userInfo);
    when(accountDAO.findByProviderIdAndProviderUserId(10L, "open_id_123")).thenReturn(null);

    ExternalAccountVO vo = new ExternalAccountVO(
        1L, 10L, "LARK", "open_id_123", 100L, null, "飞书用户",
        "https://avatar.url", "BOUND", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
    when(externalAccountService.bind(any())).thenReturn(vo);

    ExternalAccountVO result = oauthService.bindAccount(100L, "LARK", bindDTO);

    assertNotNull(result);
    assertEquals("BOUND", result.bindStatus());
  }

  @Test
  @DisplayName("bindAccount — 三方账号已被绑定时抛异常")
  void should_throw_when_bind_account_already_exists() {
    OAuthBindDTO bindDTO = new OAuthBindDTO("auth-code", "state-abc");

    when(stateService.validateAndConsumeState("state-abc")).thenReturn(stateData);
    when(providerService.getEnabledByCode("LARK")).thenReturn(providerVO);
    when(clientFactory.getClient("LARK")).thenReturn(oauthClient);
    when(oauthClient.exchangeCode("auth-code", "http://localhost:3000/oauth/callback")).thenReturn(tokenResponse);
    when(oauthClient.getUserInfo("access-token-123")).thenReturn(userInfo);

    AuthExternalAccount existing = new AuthExternalAccount();
    existing.setId(1L);
    existing.setUserId(999L);
    when(accountDAO.findByProviderIdAndProviderUserId(10L, "open_id_123")).thenReturn(existing);

    assertThrows(BusinessException.class, () -> oauthService.bindAccount(100L, "LARK", bindDTO));
  }

  @Test
  @DisplayName("unbindAccount — 正常解绑")
  void should_unbind_account_successfully() {
    AuthExternalAccount account = new AuthExternalAccount();
    account.setId(1L);
    account.setUserId(100L);

    when(accountDAO.findById(1L)).thenReturn(account);
    doNothing().when(externalAccountService).unbind(1L);

    oauthService.unbindAccount(100L, "LARK", 1L);

    verify(externalAccountService).unbind(1L);
  }

  @Test
  @DisplayName("unbindAccount — 非归属用户时抛异常")
  void should_throw_when_unbind_not_owner() {
    AuthExternalAccount account = new AuthExternalAccount();
    account.setId(1L);
    account.setUserId(999L);

    when(accountDAO.findById(1L)).thenReturn(account);

    assertThrows(BusinessException.class, () -> oauthService.unbindAccount(100L, "LARK", 1L));
  }

  @Test
  @DisplayName("unbindAccount — 账号不存在时抛异常")
  void should_throw_when_unbind_account_not_found() {
    when(accountDAO.findById(999L)).thenReturn(null);

    assertThrows(BusinessException.class, () -> oauthService.unbindAccount(100L, "LARK", 999L));
  }
}

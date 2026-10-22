package io.github.lystrosaurus.admin.auth.application;

import io.github.lystrosaurus.admin.auth.adapter.response.ExternalAccountResponse;
import io.github.lystrosaurus.admin.auth.adapter.response.OAuthAuthorizeResponse;
import io.github.lystrosaurus.admin.auth.adapter.response.OAuthLoginResponse;
import io.github.lystrosaurus.admin.auth.application.command.BindExternalAccountCommand;
import io.github.lystrosaurus.admin.auth.domain.acl.AuthProviderCatalog;
import io.github.lystrosaurus.admin.auth.domain.acl.ExternalAccountRepository;
import io.github.lystrosaurus.admin.auth.domain.acl.OAuthClient;
import io.github.lystrosaurus.admin.auth.domain.acl.OAuthClientCatalog;
import io.github.lystrosaurus.admin.auth.domain.acl.OAuthStatePort;
import io.github.lystrosaurus.admin.auth.domain.acl.OAuthTokenResponse;
import io.github.lystrosaurus.admin.auth.domain.acl.OAuthUserInfo;
import io.github.lystrosaurus.admin.auth.domain.acl.TokenSessionPort;
import io.github.lystrosaurus.admin.auth.domain.model.AuthProvider;
import io.github.lystrosaurus.admin.auth.domain.model.ExternalAccount;
import io.github.lystrosaurus.admin.auth.log.service.LoginLogService;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** OAuth 应用服务。 */
@Service
@RequiredArgsConstructor
public class OAuthApplicationService {

  private final OAuthClientCatalog clientCatalog;
  private final OAuthStatePort statePort;
  private final AuthProviderCatalog providerCatalog;
  private final ExternalAccountRepository accountRepository;
  private final ExternalAccountApplicationService externalAccountService;
  private final TokenSessionPort tokenSessionPort;
  private final LoginLogService loginLogService;

  @Value("${app.oauth.redirect-uri:http://localhost:3001}")
  private String redirectUri;

  /**
   * 发起授权。
   *
   * <p>回调地址在运行时按 {@code redirectBase + "/oauth/callback/{provider}"} 拼接，确保与前端实际访问源一致；
   * 非浏览器调用（如测试）可省略 {@code redirectBase}，回退到配置的 {@link #redirectUri}。
   *
   * @param providerCode 认证源编码
   * @param redirectBase 前端实际访问源（如 http://localhost:3001），可空
   * @return 授权 URL 与 state
   */
  public OAuthAuthorizeResponse authorize(String providerCode, String redirectBase) {
    enabledProvider(providerCode);

    OAuthClient client = clientCatalog.getClient(providerCode);
    String state = statePort.generateState();
    statePort.saveState(state, null);

    String base = (redirectBase != null && !redirectBase.isBlank()) ? redirectBase : redirectUri;
    String callbackUri = base + "/oauth/callback/" + providerCode;
    // 部分提供方（如 WECOM/WECHAT）尚未实现，buildAuthorizationUrl 会抛
    // UnsupportedOperationException，这里转译为清晰的业务错误，避免返回原始 500。
    String authorizeUrl;
    try {
      authorizeUrl = client.buildAuthorizationUrl(state, callbackUri);
    } catch (UnsupportedOperationException e) {
      throw new BusinessException(ErrorCode.OAUTH_PROVIDER_NOT_SUPPORTED);
    }

    return new OAuthAuthorizeResponse(authorizeUrl, state);
  }

  @Transactional(rollbackFor = Exception.class)
  public OAuthLoginResponse handleCallback(String providerCode, String code, String state) {
    statePort.validateAndConsumeState(state);

    AuthProvider provider = enabledProvider(providerCode);
    OAuthClient client = clientCatalog.getClient(providerCode);

    OAuthTokenResponse tokenResponse = client.exchangeCode(code, redirectUri);
    OAuthUserInfo userInfo = resolveUserInfo(client, tokenResponse);

    ExternalAccount existing =
        accountRepository
            .findByProviderIdAndProviderUserId(provider.id(), userInfo.providerUserId())
            .orElse(null);

    if (existing != null) {
      existing.touchLastLogin(LocalDateTime.now());
      accountRepository.update(existing);

      loginLogService.recordLogin(
          existing.getUserId(), "OAUTH", providerCode, null, null, true, null);

      return new OAuthLoginResponse(null, false, null);
    }

    String identifierJson =
        "{\"providerUserId\":\""
            + userInfo.providerUserId()
            + "\",\"provider\":\""
            + providerCode
            + "\"}";
    return new OAuthLoginResponse(null, true, identifierJson);
  }

  @Transactional(rollbackFor = Exception.class)
  public ExternalAccountResponse bindAccount(String providerCode, String code, String state) {
    statePort.validateAndConsumeState(state);

    AuthProvider provider = enabledProvider(providerCode);
    OAuthClient client = clientCatalog.getClient(providerCode);

    OAuthTokenResponse tokenResponse = client.exchangeCode(code, redirectUri);
    OAuthUserInfo userInfo = resolveUserInfo(client, tokenResponse);

    ExternalAccount existing =
        accountRepository
            .findByProviderIdAndProviderUserId(provider.id(), userInfo.providerUserId())
            .orElse(null);
    if (existing != null) {
      throw new BusinessException(ErrorCode.BIND_ACCOUNT_ALREADY_EXISTS);
    }

    return externalAccountService.bind(
        new BindExternalAccountCommand(
            provider.id(),
            userInfo.providerUserId(),
            null,
            userInfo.nickname(),
            userInfo.avatarUrl(),
            userInfo.identifierJson()));
  }

  @Transactional(rollbackFor = Exception.class)
  public void unbindAccount(String providerCode, Long accountId) {
    Long currentUserId = tokenSessionPort.currentUserId();
    ExternalAccount account =
        accountRepository
            .findById(accountId)
            .orElseThrow(() -> new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND));
    if (!account.getUserId().equals(currentUserId)) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND);
    }

    externalAccountService.unbind(accountId);
  }

  private OAuthUserInfo resolveUserInfo(OAuthClient client, OAuthTokenResponse tokenResponse) {
    return tokenResponse.providerUserId() != null
        ? new OAuthUserInfo(tokenResponse.providerUserId(), null, null, null)
        : client.getUserInfo(tokenResponse.accessToken());
  }

  private AuthProvider enabledProvider(String providerCode) {
    AuthProvider provider =
        providerCatalog
            .findByCode(providerCode)
            .orElseThrow(() -> new BusinessException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND));
    if (!provider.isEnabled()) {
      throw new BusinessException(ErrorCode.OAUTH_PROVIDER_DISABLED);
    }
    return provider;
  }
}

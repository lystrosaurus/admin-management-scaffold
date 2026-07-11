package io.github.lystrosaurus.admin.auth.oauth.service;

import io.github.lystrosaurus.admin.auth.external.dao.AuthExternalAccountDAO;
import io.github.lystrosaurus.admin.auth.external.dto.ExternalAccountBindDTO;
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
import io.github.lystrosaurus.admin.auth.oauth.state.OAuthStateService;
import io.github.lystrosaurus.admin.auth.oauth.state.StateData;
import io.github.lystrosaurus.admin.auth.oauth.vo.OAuthAuthorizeVO;
import io.github.lystrosaurus.admin.auth.oauth.vo.OAuthLoginVO;
import io.github.lystrosaurus.admin.auth.provider.service.AuthProviderService;
import io.github.lystrosaurus.admin.auth.provider.vo.AuthProviderVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** OAuth 服务实现 */
@Service
@RequiredArgsConstructor
public class OAuthService {

  private final OAuthClientFactory clientFactory;
  private final OAuthStateService stateService;
  private final AuthProviderService providerService;
  private final ExternalAccountService externalAccountService;
  private final AuthExternalAccountDAO accountDAO;
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
  public OAuthAuthorizeVO authorize(String providerCode, String redirectBase) {
    providerService.getEnabledByCode(providerCode);

    OAuthClient client = clientFactory.getClient(providerCode);
    String state = stateService.generateState();
    stateService.saveState(state, null);

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

    return new OAuthAuthorizeVO(authorizeUrl, state);
  }

  @Transactional(rollbackFor = Exception.class)
  public OAuthLoginVO handleCallback(String providerCode, OAuthCallbackDTO dto) {
    StateData stateData = stateService.validateAndConsumeState(dto.state());

    AuthProviderVO provider = providerService.getEnabledByCode(providerCode);
    OAuthClient client = clientFactory.getClient(providerCode);

    OAuthTokenResponse tokenResponse = client.exchangeCode(dto.code(), redirectUri);
    OAuthUserInfo userInfo =
        tokenResponse.providerUserId() != null
            ? new OAuthUserInfo(tokenResponse.providerUserId(), null, null, null)
            : client.getUserInfo(tokenResponse.accessToken());

    AuthExternalAccount existing =
        accountDAO.findByProviderIdAndProviderUserId(provider.id(), userInfo.providerUserId());

    if (existing != null) {
      existing.setLastLoginAt(java.time.LocalDateTime.now());
      accountDAO.updateById(existing);

      loginLogService.recordLogin(
          existing.getUserId(), "OAUTH", providerCode, null, null, true, null);

      return new OAuthLoginVO(null, false, null);
    }

    String identifierJson =
        "{\"providerUserId\":\""
            + userInfo.providerUserId()
            + "\",\"provider\":\""
            + providerCode
            + "\"}";
    return new OAuthLoginVO(null, true, identifierJson);
  }

  @Transactional(rollbackFor = Exception.class)
  public ExternalAccountVO bindAccount(Long userId, String providerCode, OAuthBindDTO dto) {
    stateService.validateAndConsumeState(dto.state());

    AuthProviderVO provider = providerService.getEnabledByCode(providerCode);
    OAuthClient client = clientFactory.getClient(providerCode);

    OAuthTokenResponse tokenResponse = client.exchangeCode(dto.code(), redirectUri);
    OAuthUserInfo userInfo =
        tokenResponse.providerUserId() != null
            ? new OAuthUserInfo(tokenResponse.providerUserId(), null, null, null)
            : client.getUserInfo(tokenResponse.accessToken());

    AuthExternalAccount existing =
        accountDAO.findByProviderIdAndProviderUserId(provider.id(), userInfo.providerUserId());
    if (existing != null) {
      throw new BusinessException(ErrorCode.BIND_ACCOUNT_ALREADY_EXISTS);
    }

    ExternalAccountBindDTO bindDTO =
        new ExternalAccountBindDTO(
            provider.id(),
            userInfo.providerUserId(),
            userId,
            null,
            userInfo.nickname(),
            userInfo.avatarUrl(),
            userInfo.identifierJson());
    return externalAccountService.bind(bindDTO);
  }

  @Transactional(rollbackFor = Exception.class)
  public void unbindAccount(Long userId, String providerCode, Long accountId) {
    AuthExternalAccount account = accountDAO.findById(accountId);
    if (account == null) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND);
    }
    if (!account.getUserId().equals(userId)) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND);
    }

    externalAccountService.unbind(accountId);
  }
}

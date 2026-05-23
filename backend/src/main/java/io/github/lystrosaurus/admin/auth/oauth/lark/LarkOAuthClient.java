package io.github.lystrosaurus.admin.auth.oauth.lark;

import io.github.lystrosaurus.admin.auth.oauth.OAuthClient;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthTokenResponse;
import io.github.lystrosaurus.admin.auth.oauth.dto.OAuthUserInfo;
import io.github.lystrosaurus.admin.auth.provider.service.AuthProviderService;
import io.github.lystrosaurus.admin.auth.provider.vo.AuthProviderVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** 飞书 OAuth 客户端 */
@Component("LARK")
@RequiredArgsConstructor
public class LarkOAuthClient implements OAuthClient {

  private static final String AUTHORIZE_URL =
      "https://open.feishu.cn/open-apis/authen/v1/authorize";
  private static final String APP_ACCESS_TOKEN_URL =
      "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal";
  private static final String USER_ACCESS_TOKEN_URL =
      "https://open.feishu.cn/open-apis/authen/v1/oidc/access_token";
  private static final String USER_INFO_URL =
      "https://open.feishu.cn/open-apis/authen/v1/user_info";

  private final RestTemplate restTemplate;
  private final AuthProviderService authProviderService;

  @Override
  public String getProviderCode() {
    return "LARK";
  }

  @Override
  public String buildAuthorizationUrl(String state, String redirectUri) {
    AuthProviderVO provider = authProviderService.getEnabledByCode("LARK");
    String clientId = provider.clientId();
    String scopes = provider.scopes() != null ? provider.scopes() : "openid";

    return String.format(
        "%s?app_id=%s&redirect_uri=%s&state=%s&scope=%s",
        AUTHORIZE_URL,
        URLEncoder.encode(clientId, StandardCharsets.UTF_8),
        URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
        URLEncoder.encode(state, StandardCharsets.UTF_8),
        URLEncoder.encode(scopes, StandardCharsets.UTF_8));
  }

  @Override
  @SuppressWarnings("unchecked")
  public OAuthTokenResponse exchangeCode(String code, String redirectUri) {
    AuthProviderVO provider = authProviderService.getEnabledByCode("LARK");
    String clientSecret = authProviderService.getClientSecret("LARK");

    // 第一步：获取 app_access_token
    String appAccessToken = getAppAccessToken(provider.clientId(), clientSecret);

    // 第二步：用 code 换取 user_access_token
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(appAccessToken);

    Map<String, String> body = Map.of("grant_type", "authorization_code", "code", code);
    HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

    try {
      ResponseEntity<Map> response =
          restTemplate.exchange(USER_ACCESS_TOKEN_URL, HttpMethod.POST, request, Map.class);

      Map<String, Object> data = response.getBody();
      if (data == null
          || data.containsKey("code") && !Integer.valueOf(0).equals(data.get("code"))) {
        throw new BusinessException(ErrorCode.OAUTH_CODE_EXCHANGE_FAILED);
      }

      String accessToken = (String) data.get("access_token");
      String refreshToken = (String) data.get("refresh_token");
      Long expiresIn =
          data.get("expires_in") != null ? Long.valueOf(data.get("expires_in").toString()) : null;
      String tokenType = (String) data.get("token_type");

      return new OAuthTokenResponse(accessToken, refreshToken, expiresIn, tokenType);
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw new BusinessException(
          ErrorCode.OAUTH_CODE_EXCHANGE_FAILED.getCode(), "换取 access_token 失败", e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public OAuthUserInfo getUserInfo(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    HttpEntity<Void> request = new HttpEntity<>(headers);

    try {
      ResponseEntity<Map> response =
          restTemplate.exchange(USER_INFO_URL, HttpMethod.GET, request, Map.class);

      Map<String, Object> data = response.getBody();
      if (data == null) {
        throw new BusinessException(ErrorCode.OAUTH_USERINFO_FAILED);
      }

      String openId = (String) data.get("open_id");
      String name = (String) data.get("name");
      String avatarUrl = (String) data.get("avatar_url");

      return new OAuthUserInfo(openId, name, avatarUrl, data.toString());
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.OAUTH_USERINFO_FAILED.getCode(), "获取用户信息失败", e);
    }
  }

  /**
   * 获取 app_access_token
   *
   * @param clientId 客户端ID
   * @param clientSecret 客户端密钥
   * @return app_access_token
   */
  @SuppressWarnings("unchecked")
  private String getAppAccessToken(String clientId, String clientSecret) {
    Map<String, String> body = Map.of("app_id", clientId, "app_secret", clientSecret);

    try {
      Map<String, Object> response =
          restTemplate.postForObject(APP_ACCESS_TOKEN_URL, body, Map.class);

      if (response == null || !Integer.valueOf(0).equals(response.get("code"))) {
        throw new BusinessException(ErrorCode.OAUTH_CODE_EXCHANGE_FAILED);
      }

      return (String) response.get("app_access_token");
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw new BusinessException(
          ErrorCode.OAUTH_CODE_EXCHANGE_FAILED.getCode(), "获取 app_access_token 失败", e);
    }
  }
}

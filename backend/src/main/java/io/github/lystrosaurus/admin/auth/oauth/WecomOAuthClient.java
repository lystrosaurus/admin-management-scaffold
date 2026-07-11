package io.github.lystrosaurus.admin.auth.oauth;

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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** 企业微信 OAuth 客户端（网页授权 snsapi_base） */
@Component("WECOM")
@RequiredArgsConstructor
public class WecomOAuthClient implements OAuthClient {

  private static final String AUTHORIZE_URL =
      "https://open.weixin.qq.com/connect/oauth2/authorize";
  private static final String GET_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
  private static final String GET_USER_INFO_URL =
      "https://qyapi.weixin.qq.com/cgi-bin/oauth2/getuserinfo";

  private final RestTemplate restTemplate;
  private final AuthProviderService authProviderService;

  @Override
  public String getProviderCode() {
    return "WECOM";
  }

  @Override
  public String buildAuthorizationUrl(String state, String redirectUri) {
    AuthProviderVO provider = authProviderService.getEnabledByCode("WECOM");
    // 企业微信网页授权使用 CorpID 作为 appid（不是 AgentID）
    String corpId = provider.clientId();
    // 企业微信网页授权固定使用 snsapi_base（种子里的 openid 是飞书写法，此处不读取）
    String scopes = "snsapi_base";

    return String.format(
        "%s?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
        AUTHORIZE_URL,
        URLEncoder.encode(corpId, StandardCharsets.UTF_8),
        URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
        URLEncoder.encode(scopes, StandardCharsets.UTF_8),
        URLEncoder.encode(state, StandardCharsets.UTF_8));
  }

  @Override
  @SuppressWarnings("unchecked")
  public OAuthTokenResponse exchangeCode(String code, String redirectUri) {
    AuthProviderVO provider = authProviderService.getEnabledByCode("WECOM");
    String corpId = provider.clientId();
    String corpSecret = authProviderService.getClientSecret("WECOM");

    // 第一步：获取企业凭证 access_token（corpid + corpsecret）
    String accessToken = getCorpAccessToken(corpId, corpSecret);

    // 第二步：用 code 换取成员 userid（snsapi_base 仅返回 userid）
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", "application/json");
    HttpEntity<Void> request = new HttpEntity<>(headers);

    try {
      ResponseEntity<Map> response =
          restTemplate.exchange(
              GET_USER_INFO_URL + "?access_token=" + accessToken + "&code=" + code,
              HttpMethod.GET,
              request,
              Map.class);

      Map<String, Object> data = response.getBody();
      if (data == null
          || data.containsKey("errcode") && !Integer.valueOf(0).equals(data.get("errcode"))) {
        throw new BusinessException(ErrorCode.OAUTH_CODE_EXCHANGE_FAILED);
      }

      String userId = (String) data.get("UserId");
      if (userId == null) {
        throw new BusinessException(ErrorCode.OAUTH_CODE_EXCHANGE_FAILED);
      }

      // 企业微信网页授权 snsapi_base 仅返回 userid；nickname/avatar 需 snsapi_privateinfo
      // + user_ticket 换取。此处以 userid 作为三方账号标识，并写入 tokenResponse.providerUserId，
      // 供 OAuthService 直接采用（getUserInfo 无需再调用）。
      return new OAuthTokenResponse(accessToken, null, 7200L, "WeCom", userId);
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw new BusinessException(
          ErrorCode.OAUTH_CODE_EXCHANGE_FAILED.getCode(), "企业微信换取用户信息失败", e);
    }
  }

  @Override
  public OAuthUserInfo getUserInfo(String accessToken) {
    // 企业微信的账号标识（userid）已在 exchangeCode 阶段随 code 获取并写入
    // tokenResponse.providerUserId，OAuthService 会优先采用该值，故此分支不会被走到。
    return new OAuthUserInfo(null, null, null, null);
  }

  @SuppressWarnings("unchecked")
  private String getCorpAccessToken(String corpId, String corpSecret) {
    try {
      Map<String, Object> response =
          restTemplate.getForObject(
              GET_TOKEN_URL + "?corpid=" + corpId + "&corpsecret=" + corpSecret, Map.class);

      if (response == null || !Integer.valueOf(0).equals(response.get("errcode"))) {
        throw new BusinessException(ErrorCode.OAUTH_CODE_EXCHANGE_FAILED);
      }

      return (String) response.get("access_token");
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw new BusinessException(
          ErrorCode.OAUTH_CODE_EXCHANGE_FAILED.getCode(), "企业微信获取 access_token 失败", e);
    }
  }
}

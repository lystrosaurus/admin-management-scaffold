package io.github.lystrosaurus.admin.auth.oauth.vo;

/**
 * OAuth 登录 VO
 *
 * @param accessToken access_token（登录成功时）
 * @param needBind 是否需要绑定
 * @param externalUserIdentity 外部用户标识（需要绑定时）
 */
public record OAuthLoginVO(String accessToken, boolean needBind, String externalUserIdentity) {}

package io.github.lystrosaurus.admin.auth.adapter.response;

/**
 * OAuth 登录响应。
 *
 * @param accessToken access_token（登录成功时）
 * @param needBind 是否需要绑定
 * @param externalUserIdentity 外部用户标识（需要绑定时）
 */
public record OAuthLoginResponse(String accessToken, boolean needBind, String externalUserIdentity) {}

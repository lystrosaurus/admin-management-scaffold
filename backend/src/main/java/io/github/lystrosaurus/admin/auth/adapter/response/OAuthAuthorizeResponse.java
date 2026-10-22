package io.github.lystrosaurus.admin.auth.adapter.response;

/**
 * OAuth 授权响应。
 *
 * @param url 授权URL
 * @param state 状态值
 */
public record OAuthAuthorizeResponse(String url, String state) {}

package io.github.lystrosaurus.admin.auth.adapter.request;

/**
 * OAuth 回调请求（GET query 参数绑定）。
 *
 * @param code 授权码
 * @param state 状态值
 */
public record OAuthCallbackRequest(String code, String state) {}

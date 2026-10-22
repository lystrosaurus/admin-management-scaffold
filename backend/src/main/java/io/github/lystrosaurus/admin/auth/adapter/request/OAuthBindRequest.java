package io.github.lystrosaurus.admin.auth.adapter.request;

/**
 * OAuth 绑定请求。
 *
 * @param code 授权码
 * @param state 状态值
 */
public record OAuthBindRequest(String code, String state) {}

package io.github.lystrosaurus.admin.auth.oauth.vo;

/**
 * OAuth 授权 VO
 *
 * @param authorizeUrl 授权URL
 * @param state 状态值
 */
public record OAuthAuthorizeVO(String authorizeUrl, String state) {}

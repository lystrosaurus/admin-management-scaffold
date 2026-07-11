package io.github.lystrosaurus.admin.auth.oauth.vo;

/**
 * OAuth 授权 VO
 *
 * @param url 授权URL
 * @param state 状态值
 */
public record OAuthAuthorizeVO(String url, String state) {}

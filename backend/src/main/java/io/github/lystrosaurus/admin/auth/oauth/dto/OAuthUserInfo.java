package io.github.lystrosaurus.admin.auth.oauth.dto;

/**
 * OAuth 用户信息
 *
 * @param providerUserId 提供方用户ID
 * @param nickname 昵称
 * @param avatarUrl 头像URL
 * @param identifierJson 标识符JSON
 */
public record OAuthUserInfo(
    String providerUserId, String nickname, String avatarUrl, String identifierJson) {}

package io.github.lystrosaurus.admin.auth.oauth.dto;

/**
 * OAuth Token 响应
 *
 * @param accessToken access_token
 * @param refreshToken refresh_token
 * @param expiresIn 过期时间（秒）
 * @param tokenType token 类型
 */
public record OAuthTokenResponse(
    String accessToken, String refreshToken, Long expiresIn, String tokenType) {}

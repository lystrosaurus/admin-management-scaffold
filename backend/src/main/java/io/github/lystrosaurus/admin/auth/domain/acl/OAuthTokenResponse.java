package io.github.lystrosaurus.admin.auth.domain.acl;

/**
 * OAuth Token 响应（端口返回值对象）。
 *
 * @param accessToken access_token
 * @param refreshToken refresh_token
 * @param expiresIn 过期时间（秒）
 * @param tokenType token 类型
 * @param providerUserId 提供方用户ID（部分提供方在 code 换取阶段即可获得，如企业微信；为 null 时由 getUserInfo 获取）
 */
public record OAuthTokenResponse(
    String accessToken, String refreshToken, Long expiresIn, String tokenType, String providerUserId) {}

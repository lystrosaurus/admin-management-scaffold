package io.github.lystrosaurus.admin.auth.adapter.response;

import io.github.lystrosaurus.admin.system.user.adapter.response.UserResponse;

/**
 * 登录响应（字段与旧 LoginVO 完全一致，保证 API 契约不变）
 *
 * @param accessToken 访问令牌
 * @param user 用户信息
 */
public record LoginResponse(String accessToken, UserResponse user) {}

package io.github.lystrosaurus.admin.auth.vo;

import io.github.lystrosaurus.admin.system.user.adapter.response.UserResponse;

/**
 * 登录VO
 *
 * @param accessToken 访问令牌
 * @param user 用户信息
 */
public record LoginVO(String accessToken, UserResponse user) {}

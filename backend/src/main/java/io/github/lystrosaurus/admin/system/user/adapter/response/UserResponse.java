package io.github.lystrosaurus.admin.system.user.adapter.response;

import java.time.LocalDateTime;

/**
 * 用户响应（字段集合与原 UserVO 完全一致，保证 API 契约不变）。
 *
 * @param id 主键ID
 * @param username 用户名
 * @param nickname 昵称
 * @param phone 手机号
 * @param email 邮箱
 * @param status 状态
 * @param lastLoginAt 最后登录时间
 * @param createdAt 创建时间
 */
public record UserResponse(
    Long id,
    String username,
    String nickname,
    String phone,
    String email,
    String status,
    LocalDateTime lastLoginAt,
    LocalDateTime createdAt) {}

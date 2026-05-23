package io.github.lystrosaurus.admin.system.user.vo;

import java.time.LocalDateTime;

/**
 * 用户VO
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
public record UserVO(
    Long id,
    String username,
    String nickname,
    String phone,
    String email,
    String status,
    LocalDateTime lastLoginAt,
    LocalDateTime createdAt) {}

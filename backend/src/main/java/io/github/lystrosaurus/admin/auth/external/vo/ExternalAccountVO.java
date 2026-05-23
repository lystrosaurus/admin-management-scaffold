package io.github.lystrosaurus.admin.auth.external.vo;

import java.time.LocalDateTime;

/**
 * 三方账号VO
 *
 * @param id 主键ID
 * @param providerId 认证源ID
 * @param providerCode 认证源编码（关联查询）
 * @param providerUserId 第三方平台用户ID
 * @param userId 关联的本地用户ID
 * @param employeeId 关联的员工ID
 * @param nickname 第三方昵称
 * @param avatarUrl 第三方头像URL
 * @param bindStatus 绑定状态
 * @param lastLoginAt 最后登录时间
 * @param createdAt 创建时间
 * @param updatedAt 更新时间
 */
public record ExternalAccountVO(
    Long id,
    Long providerId,
    String providerCode,
    String providerUserId,
    Long userId,
    Long employeeId,
    String nickname,
    String avatarUrl,
    String bindStatus,
    LocalDateTime lastLoginAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}

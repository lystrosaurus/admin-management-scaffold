package io.github.lystrosaurus.admin.auth.external.dto;

/**
 * 三方账号绑定DTO
 *
 * @param providerId 认证源ID
 * @param providerUserId 第三方平台用户ID
 * @param userId 关联的本地用户ID
 * @param employeeId 关联的员工ID
 * @param nickname 第三方昵称
 * @param avatarUrl 第三方头像URL
 * @param identifierJson 标识信息JSON
 */
public record ExternalAccountBindDTO(
    Long providerId,
    String providerUserId,
    Long userId,
    Long employeeId,
    String nickname,
    String avatarUrl,
    String identifierJson) {}

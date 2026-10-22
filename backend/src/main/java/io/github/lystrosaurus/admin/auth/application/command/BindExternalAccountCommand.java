package io.github.lystrosaurus.admin.auth.application.command;

/**
 * 三方账号绑定命令。
 *
 * <p>归属始终以当前登录用户为准，故不包含 userId。
 *
 * @param providerId 认证源ID
 * @param providerUserId 第三方平台用户ID
 * @param employeeId 关联的员工ID
 * @param nickname 第三方昵称
 * @param avatarUrl 第三方头像URL
 * @param identifierJson 标识信息JSON
 */
public record BindExternalAccountCommand(
    Long providerId,
    String providerUserId,
    Long employeeId,
    String nickname,
    String avatarUrl,
    String identifierJson) {}

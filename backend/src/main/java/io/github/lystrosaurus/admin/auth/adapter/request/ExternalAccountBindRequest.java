package io.github.lystrosaurus.admin.auth.adapter.request;

import io.github.lystrosaurus.admin.auth.application.command.BindExternalAccountCommand;

/**
 * 三方账号绑定请求。
 *
 * <p>保留 userId 字段以维持既有 wire 契约；服务端始终以当前登录用户为准，不采信该字段。
 *
 * @param providerId 认证源ID
 * @param providerUserId 第三方平台用户ID
 * @param userId 关联的本地用户ID（服务端忽略）
 * @param employeeId 关联的员工ID
 * @param nickname 第三方昵称
 * @param avatarUrl 第三方头像URL
 * @param identifierJson 标识信息JSON
 */
public record ExternalAccountBindRequest(
    Long providerId,
    String providerUserId,
    Long userId,
    Long employeeId,
    String nickname,
    String avatarUrl,
    String identifierJson) {

  /** 转换为应用层命令（丢弃不安全的 userId） */
  public BindExternalAccountCommand toCommand() {
    return new BindExternalAccountCommand(
        providerId, providerUserId, employeeId, nickname, avatarUrl, identifierJson);
  }
}

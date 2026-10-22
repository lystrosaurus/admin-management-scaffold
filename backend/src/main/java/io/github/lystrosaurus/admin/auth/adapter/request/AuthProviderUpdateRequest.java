package io.github.lystrosaurus.admin.auth.adapter.request;

import io.github.lystrosaurus.admin.auth.application.command.AuthProviderUpdateCommand;

/**
 * 认证源更新请求（全部可选字段）。
 *
 * @param name 认证源名称
 * @param clientId 客户端ID
 * @param clientSecretEncrypted 客户端密钥（加密）
 * @param redirectUri 回调地址
 * @param scopes 授权范围
 * @param enabled 是否启用
 * @param configJson 扩展配置JSON
 */
public record AuthProviderUpdateRequest(
    String name,
    String clientId,
    String clientSecretEncrypted,
    String redirectUri,
    String scopes,
    Integer enabled,
    String configJson) {

  /** 转换为应用层命令 */
  public AuthProviderUpdateCommand toCommand() {
    return new AuthProviderUpdateCommand(
        name, clientId, clientSecretEncrypted, redirectUri, scopes, enabled, configJson);
  }
}

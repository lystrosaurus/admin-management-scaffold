package io.github.lystrosaurus.admin.auth.application.command;

/**
 * 认证源创建命令。
 *
 * @param code 认证源编码
 * @param name 认证源名称
 * @param clientId 客户端ID
 * @param clientSecretEncrypted 客户端密钥（加密）
 * @param redirectUri 回调地址
 * @param scopes 授权范围
 * @param enabled 是否启用
 * @param configJson 扩展配置JSON
 */
public record AuthProviderCreateCommand(
    String code,
    String name,
    String clientId,
    String clientSecretEncrypted,
    String redirectUri,
    String scopes,
    Integer enabled,
    String configJson) {}

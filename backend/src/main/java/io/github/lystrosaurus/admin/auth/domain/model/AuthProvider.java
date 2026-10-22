package io.github.lystrosaurus.admin.auth.domain.model;

import java.time.LocalDateTime;

/**
 * 认证源领域对象。
 *
 * <p>对应 auth_provider 表；零框架注解，字段与持久化对象逐字段一致。
 *
 * @param id 主键ID
 * @param code 认证源编码
 * @param name 认证源名称
 * @param clientId 客户端ID
 * @param clientSecretEncrypted 客户端密钥（加密存储）
 * @param redirectUri 回调地址
 * @param scopes 授权范围
 * @param enabled 是否启用（1=启用）
 * @param configJson 扩展配置JSON
 * @param createdAt 创建时间
 * @param updatedAt 更新时间
 */
public record AuthProvider(
    Long id,
    String code,
    String name,
    String clientId,
    String clientSecretEncrypted,
    String redirectUri,
    String scopes,
    Integer enabled,
    String configJson,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  /** 是否启用 */
  public boolean isEnabled() {
    return Integer.valueOf(1).equals(enabled);
  }

  /**
   * 部分更新：仅非空字段生效，返回更新后的认证源。
   *
   * @param name 认证源名称
   * @param clientId 客户端ID
   * @param clientSecretEncrypted 客户端密钥（加密）
   * @param redirectUri 回调地址
   * @param scopes 授权范围
   * @param enabled 是否启用
   * @param configJson 扩展配置JSON
   * @return 更新后的认证源
   */
  public AuthProvider update(
      String name,
      String clientId,
      String clientSecretEncrypted,
      String redirectUri,
      String scopes,
      Integer enabled,
      String configJson) {
    return new AuthProvider(
        id,
        code,
        name != null ? name : this.name,
        clientId != null ? clientId : this.clientId,
        clientSecretEncrypted != null ? clientSecretEncrypted : this.clientSecretEncrypted,
        redirectUri != null ? redirectUri : this.redirectUri,
        scopes != null ? scopes : this.scopes,
        enabled != null ? enabled : this.enabled,
        configJson != null ? configJson : this.configJson,
        createdAt,
        updatedAt);
  }
}

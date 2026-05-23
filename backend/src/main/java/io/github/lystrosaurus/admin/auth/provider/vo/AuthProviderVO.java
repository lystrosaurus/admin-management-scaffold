package io.github.lystrosaurus.admin.auth.provider.vo;

import java.time.LocalDateTime;

/**
 * 认证源VO
 *
 * <p>安全要求：不返回 clientSecretEncrypted。
 *
 * @param id 主键ID
 * @param code 认证源编码
 * @param name 认证源名称
 * @param clientId 客户端ID
 * @param redirectUri 回调地址
 * @param scopes 授权范围
 * @param enabled 是否启用
 * @param configJson 扩展配置JSON
 * @param createdAt 创建时间
 * @param updatedAt 更新时间
 */
public record AuthProviderVO(
    Long id,
    String code,
    String name,
    String clientId,
    String redirectUri,
    String scopes,
    Integer enabled,
    String configJson,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}

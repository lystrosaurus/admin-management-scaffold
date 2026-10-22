package io.github.lystrosaurus.admin.integration.principal.adapter.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 外部主体响应（字段与原 ExtPrincipalVO 完全一致，保证 API 契约不变）。
 *
 * @param id 主键ID
 * @param sourceId 外部系统ID
 * @param sourceName 外部系统名称（关联查询）
 * @param principalType 主体类型
 * @param externalKey 外部系统主键
 * @param displayName 显示名称
 * @param status 状态
 * @param lastSyncAt 最后同步时间
 * @param canonicalType 映射目标类型
 * @param canonicalId 映射目标ID
 * @param linkStatus 关联状态
 * @param createdAt 创建时间
 * @param updatedAt 更新时间
 * @param identifiers 标识符列表
 */
public record ExtPrincipalResponse(
    Long id,
    Long sourceId,
    String sourceName,
    String principalType,
    String externalKey,
    String displayName,
    String status,
    LocalDateTime lastSyncAt,
    String canonicalType,
    Long canonicalId,
    String linkStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<ExtPrincipalIdentifierResponse> identifiers) {}

package io.github.lystrosaurus.admin.integration.principal.vo;

import java.time.LocalDateTime;
import java.util.List;

/** 外部主体VO */
public record ExtPrincipalVO(
    /** 主键ID */
    Long id,
    /** 外部系统ID */
    Long sourceId,
    /** 外部系统名称（关联查询） */
    String sourceName,
    /** 主体类型 */
    String principalType,
    /** 外部系统主键 */
    String externalKey,
    /** 显示名称 */
    String displayName,
    /** 状态 */
    String status,
    /** 最后同步时间 */
    LocalDateTime lastSyncAt,
    /** 映射目标类型 */
    String canonicalType,
    /** 映射目标ID */
    Long canonicalId,
    /** 关联状态 */
    String linkStatus,
    /** 创建时间 */
    LocalDateTime createdAt,
    /** 更新时间 */
    LocalDateTime updatedAt,
    /** 标识符列表 */
    List<ExtPrincipalIdentifierVO> identifiers) {}

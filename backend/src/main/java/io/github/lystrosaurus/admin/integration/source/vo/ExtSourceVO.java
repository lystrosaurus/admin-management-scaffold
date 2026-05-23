package io.github.lystrosaurus.admin.integration.source.vo;

import java.time.LocalDateTime;

/** 外部身份源VO */
public record ExtSourceVO(
    /** 主键ID */
    Long id,
    /** 系统编码 */
    String code,
    /** 系统名称 */
    String name,
    /** 来源类型 */
    String sourceType,
    /** 租户标识 */
    String tenantKey,
    /** 状态 */
    String status,
    /** 优先级 */
    Integer priority,
    /** 扩展配置JSON */
    String configJson,
    /** 创建时间 */
    LocalDateTime createdAt,
    /** 更新时间 */
    LocalDateTime updatedAt) {}

package io.github.lystrosaurus.admin.integration.source.dto;

/** 外部身份源创建DTO */
public record ExtSourceCreateDTO(
    /** 系统编码 */
    String code,
    /** 系统名称 */
    String name,
    /** 来源类型(HR/IM/OA) */
    String sourceType,
    /** 租户标识 */
    String tenantKey,
    /** 优先级 */
    Integer priority,
    /** 扩展配置JSON */
    String configJson) {}

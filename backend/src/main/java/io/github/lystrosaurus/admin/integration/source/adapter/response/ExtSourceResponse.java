package io.github.lystrosaurus.admin.integration.source.adapter.response;

import java.time.LocalDateTime;

/**
 * 外部身份源响应（字段与原 ExtSourceVO 完全一致，保证 API 契约不变）。
 *
 * @param id 主键ID
 * @param code 系统编码
 * @param name 系统名称
 * @param sourceType 来源类型
 * @param tenantKey 租户标识
 * @param status 状态
 * @param priority 优先级
 * @param configJson 扩展配置JSON
 * @param createdAt 创建时间
 * @param updatedAt 更新时间
 */
public record ExtSourceResponse(
    Long id,
    String code,
    String name,
    String sourceType,
    String tenantKey,
    String status,
    Integer priority,
    String configJson,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}

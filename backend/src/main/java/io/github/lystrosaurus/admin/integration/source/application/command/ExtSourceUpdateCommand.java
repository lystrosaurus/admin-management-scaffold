package io.github.lystrosaurus.admin.integration.source.application.command;

/**
 * 外部身份源更新命令。
 *
 * @param name 系统名称
 * @param sourceType 来源类型(HR/IM/OA)
 * @param tenantKey 租户标识
 * @param status 状态(ENABLED/DISABLED)
 * @param priority 优先级
 * @param configJson 扩展配置JSON
 */
public record ExtSourceUpdateCommand(
    String name,
    String sourceType,
    String tenantKey,
    String status,
    Integer priority,
    String configJson) {}

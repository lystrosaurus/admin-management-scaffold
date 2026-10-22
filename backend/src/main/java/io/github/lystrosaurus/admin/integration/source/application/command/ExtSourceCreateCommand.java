package io.github.lystrosaurus.admin.integration.source.application.command;

/**
 * 外部身份源创建命令。
 *
 * @param code 系统编码
 * @param name 系统名称
 * @param sourceType 来源类型(HR/IM/OA)
 * @param tenantKey 租户标识
 * @param priority 优先级
 * @param configJson 扩展配置JSON
 */
public record ExtSourceCreateCommand(
    String code,
    String name,
    String sourceType,
    String tenantKey,
    Integer priority,
    String configJson) {}

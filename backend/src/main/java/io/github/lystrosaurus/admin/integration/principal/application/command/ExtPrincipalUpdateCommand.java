package io.github.lystrosaurus.admin.integration.principal.application.command;

/**
 * 外部主体更新命令。
 *
 * @param displayName 显示名称
 * @param status 状态(ACTIVE/INACTIVE)
 * @param canonicalType 映射目标类型(EMPLOYEE/ORG_UNIT)
 * @param canonicalId 映射目标ID
 * @param linkStatus 关联状态
 */
public record ExtPrincipalUpdateCommand(
    String displayName, String status, String canonicalType, Long canonicalId, String linkStatus) {}

package io.github.lystrosaurus.admin.system.role.application.command;

/**
 * 更新角色命令。
 *
 * @param name 角色名称
 * @param description 描述
 * @param sortOrder 排序号
 * @param status 状态
 * @param dataScopeType 数据权限范围
 */
public record RoleUpdateCommand(
    String name, String description, Integer sortOrder, String status, String dataScopeType) {}

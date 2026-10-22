package io.github.lystrosaurus.admin.system.role.application.command;

/**
 * 创建角色命令。
 *
 * @param code 角色编码
 * @param name 角色名称
 * @param description 描述
 * @param sortOrder 排序号
 * @param dataScopeType 数据权限范围
 */
public record RoleCreateCommand(
    String code, String name, String description, Integer sortOrder, String dataScopeType) {}

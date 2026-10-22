package io.github.lystrosaurus.admin.organization.orgunit.application.command;

/**
 * 组织单元更新命令（所有字段可选，支持部分更新）。
 *
 * @param name 组织名称
 * @param parentId 父节点ID
 * @param managerEmployeeId 负责人员工ID
 * @param sortOrder 排序号
 * @param status 状态
 */
public record OrgUnitUpdateCommand(
    String name, Long parentId, Long managerEmployeeId, Integer sortOrder, String status) {}

package io.github.lystrosaurus.admin.organization.orgunit.application.command;

/**
 * 组织单元创建命令。
 *
 * @param code 组织编码
 * @param name 组织名称
 * @param parentId 父节点ID(默认0表示根节点)
 * @param managerEmployeeId 负责人员工ID
 * @param sortOrder 排序号
 */
public record OrgUnitCreateCommand(
    String code, String name, Long parentId, Long managerEmployeeId, Integer sortOrder) {}

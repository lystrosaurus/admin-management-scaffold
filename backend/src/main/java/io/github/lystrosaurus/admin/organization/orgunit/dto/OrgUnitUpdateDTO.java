package io.github.lystrosaurus.admin.organization.orgunit.dto;

/**
 * 组织单元更新DTO(所有字段可选，支持部分更新)
 *
 * @param name 组织名称
 * @param parentId 父节点ID
 * @param managerEmployeeId 负责人员工ID
 * @param sortOrder 排序号
 * @param status 状态
 */
public record OrgUnitUpdateDTO(
    String name, Long parentId, Long managerEmployeeId, Integer sortOrder, String status) {}

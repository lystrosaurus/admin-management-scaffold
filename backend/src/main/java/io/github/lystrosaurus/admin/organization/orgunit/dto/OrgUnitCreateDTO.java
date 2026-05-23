package io.github.lystrosaurus.admin.organization.orgunit.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 组织单元创建DTO
 *
 * @param code 组织编码
 * @param name 组织名称
 * @param parentId 父节点ID(默认0表示根节点)
 * @param managerEmployeeId 负责人员工ID
 * @param sortOrder 排序号
 */
public record OrgUnitCreateDTO(
    @NotBlank(message = "组织编码不能为空") String code,
    @NotBlank(message = "组织名称不能为空") String name,
    Long parentId,
    Long managerEmployeeId,
    Integer sortOrder) {}

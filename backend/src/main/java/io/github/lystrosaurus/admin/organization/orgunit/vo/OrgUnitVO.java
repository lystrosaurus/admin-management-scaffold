package io.github.lystrosaurus.admin.organization.orgunit.vo;

import java.time.LocalDateTime;

/**
 * 组织单元VO
 *
 * @param id 主键ID
 * @param code 组织编码
 * @param name 组织名称
 * @param parentId 父节点ID
 * @param fullPath 完整路径
 * @param level 层级
 * @param managerEmployeeId 负责人员工ID
 * @param sortOrder 排序号
 * @param status 状态
 * @param createdAt 创建时间
 */
public record OrgUnitVO(
    Long id,
    String code,
    String name,
    Long parentId,
    String fullPath,
    Integer level,
    Long managerEmployeeId,
    Integer sortOrder,
    String status,
    LocalDateTime createdAt) {}

package io.github.lystrosaurus.admin.organization.orgunit.dto;

/**
 * 组织单元查询DTO
 *
 * @param keyword 关键词(模糊匹配名称/编码)
 * @param status 状态
 * @param parentId 父节点ID
 */
public record OrgUnitQueryDTO(String keyword, String status, Long parentId) {}

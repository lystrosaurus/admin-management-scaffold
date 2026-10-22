package io.github.lystrosaurus.admin.organization.orgunit.application.command;

/**
 * 组织单元查询命令。
 *
 * @param keyword 关键词(模糊匹配名称/编码)
 * @param status 状态
 * @param parentId 父节点ID
 */
public record OrgUnitQuery(String keyword, String status, Long parentId) {}

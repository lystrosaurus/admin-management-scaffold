package io.github.lystrosaurus.admin.organization.orgunit.adapter.response;

import java.util.List;

/**
 * 组织单元树形响应（字段集合与原 OrgUnitTreeVO 完全一致）。
 *
 * @param id 主键ID
 * @param code 组织编码
 * @param name 组织名称
 * @param parentId 父节点ID
 * @param level 层级
 * @param managerEmployeeId 负责人员工ID
 * @param sortOrder 排序号
 * @param status 状态
 * @param leaderName 负责人姓名
 * @param children 子节点列表
 */
public record OrgUnitTreeResponse(
    Long id,
    String code,
    String name,
    Long parentId,
    Integer level,
    Long managerEmployeeId,
    Integer sortOrder,
    String status,
    String leaderName,
    List<OrgUnitTreeResponse> children) {}

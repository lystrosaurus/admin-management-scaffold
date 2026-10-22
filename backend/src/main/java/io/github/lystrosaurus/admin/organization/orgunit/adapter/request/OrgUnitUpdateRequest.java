package io.github.lystrosaurus.admin.organization.orgunit.adapter.request;

import io.github.lystrosaurus.admin.organization.orgunit.application.command.OrgUnitUpdateCommand;

/**
 * 组织单元更新请求（字段与原 OrgUnitUpdateDTO 一致）。
 *
 * @param name 组织名称
 * @param parentId 父节点ID
 * @param managerEmployeeId 负责人员工ID
 * @param sortOrder 排序号
 * @param status 状态
 */
public record OrgUnitUpdateRequest(
    String name, Long parentId, Long managerEmployeeId, Integer sortOrder, String status) {

  /** 转为应用层命令 */
  public OrgUnitUpdateCommand toCommand() {
    return new OrgUnitUpdateCommand(name, parentId, managerEmployeeId, sortOrder, status);
  }
}

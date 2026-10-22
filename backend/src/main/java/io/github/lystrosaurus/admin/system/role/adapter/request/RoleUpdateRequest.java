package io.github.lystrosaurus.admin.system.role.adapter.request;

import io.github.lystrosaurus.admin.system.role.application.command.RoleUpdateCommand;

/**
 * 角色更新请求（字段与原 RoleUpdateDTO 一致）。
 *
 * @param name 角色名称
 * @param description 描述
 * @param sortOrder 排序号
 * @param status 状态
 * @param dataScopeType 数据权限范围
 */
public record RoleUpdateRequest(
    String name, String description, Integer sortOrder, String status, String dataScopeType) {

  /** 转为应用层命令 */
  public RoleUpdateCommand toCommand() {
    return new RoleUpdateCommand(name, description, sortOrder, status, dataScopeType);
  }
}

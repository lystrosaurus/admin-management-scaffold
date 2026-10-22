package io.github.lystrosaurus.admin.system.permission.adapter.request;

import io.github.lystrosaurus.admin.system.permission.application.command.PermissionUpdateCommand;

/**
 * 权限更新请求（字段与原 PermissionUpdateDTO 一致）。
 *
 * @param name 权限名称
 * @param type 类型
 * @param module 所属模块
 * @param resource 资源标识
 * @param action 操作类型
 * @param status 状态
 */
public record PermissionUpdateRequest(
    String name, String type, String module, String resource, String action, String status) {

  /** 转为应用层命令 */
  public PermissionUpdateCommand toCommand() {
    return new PermissionUpdateCommand(name, type, module, resource, action, status);
  }
}

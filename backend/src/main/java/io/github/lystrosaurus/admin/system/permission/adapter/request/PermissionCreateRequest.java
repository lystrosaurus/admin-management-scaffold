package io.github.lystrosaurus.admin.system.permission.adapter.request;

import io.github.lystrosaurus.admin.system.permission.application.command.PermissionCreateCommand;
import jakarta.validation.constraints.NotBlank;

/**
 * 权限创建请求（字段与原 PermissionCreateDTO 一致）。
 *
 * @param code 权限编码
 * @param name 权限名称
 * @param type 类型
 * @param module 所属模块
 * @param resource 资源标识
 * @param action 操作类型
 */
public record PermissionCreateRequest(
    @NotBlank(message = "权限编码不能为空") String code,
    @NotBlank(message = "权限名称不能为空") String name,
    String type,
    String module,
    String resource,
    String action) {

  /** 转为应用层命令 */
  public PermissionCreateCommand toCommand() {
    return new PermissionCreateCommand(code, name, type, module, resource, action);
  }
}

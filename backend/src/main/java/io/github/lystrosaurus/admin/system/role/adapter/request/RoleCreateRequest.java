package io.github.lystrosaurus.admin.system.role.adapter.request;

import io.github.lystrosaurus.admin.system.role.application.command.RoleCreateCommand;
import jakarta.validation.constraints.NotBlank;

/**
 * 角色创建请求（字段与原 RoleCreateDTO 一致）。
 *
 * @param code 角色编码
 * @param name 角色名称
 * @param description 描述
 * @param sortOrder 排序号
 * @param dataScopeType 数据权限范围
 */
public record RoleCreateRequest(
    @NotBlank(message = "角色编码不能为空") String code,
    @NotBlank(message = "角色名称不能为空") String name,
    String description,
    Integer sortOrder,
    String dataScopeType) {

  /** 转为应用层命令 */
  public RoleCreateCommand toCommand() {
    return new RoleCreateCommand(code, name, description, sortOrder, dataScopeType);
  }
}

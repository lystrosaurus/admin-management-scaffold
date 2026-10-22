package io.github.lystrosaurus.admin.system.menu.adapter.request;

import io.github.lystrosaurus.admin.system.menu.application.command.MenuUpdateCommand;

/**
 * 菜单更新请求（字段与原 MenuUpdateDTO 一致）。
 *
 * @param name 菜单名称
 * @param path 路由路径
 * @param component 前端组件路径
 * @param icon 图标
 * @param sortOrder 排序号
 * @param type 类型
 * @param permissionCode 权限码
 * @param visible 可见性
 * @param status 状态
 */
public record MenuUpdateRequest(
    String name,
    String path,
    String component,
    String icon,
    Integer sortOrder,
    Byte type,
    String permissionCode,
    Byte visible,
    Byte status) {

  /** 转为应用层命令 */
  public MenuUpdateCommand toCommand() {
    return new MenuUpdateCommand(
        name, path, component, icon, sortOrder, type, permissionCode, visible, status);
  }
}

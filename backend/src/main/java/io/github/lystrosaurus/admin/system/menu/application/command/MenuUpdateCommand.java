package io.github.lystrosaurus.admin.system.menu.application.command;

/**
 * 更新菜单命令。
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
public record MenuUpdateCommand(
    String name,
    String path,
    String component,
    String icon,
    Integer sortOrder,
    Byte type,
    String permissionCode,
    Byte visible,
    Byte status) {}

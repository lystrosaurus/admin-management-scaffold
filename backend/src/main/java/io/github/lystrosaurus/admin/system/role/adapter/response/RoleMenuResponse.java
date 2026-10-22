package io.github.lystrosaurus.admin.system.role.adapter.response;

import java.util.List;

/**
 * 角色详情中的菜单项（字段集合与原 MenuVO 完全一致）。
 *
 * @param id 主键ID
 * @param parentId 父菜单ID
 * @param name 菜单名称
 * @param path 路由路径
 * @param component 前端组件路径
 * @param icon 图标
 * @param sortOrder 排序号
 * @param type 类型
 * @param permissionCode 权限码
 * @param visible 可见性
 * @param status 状态
 * @param children 子菜单列表
 */
public record RoleMenuResponse(
    Long id,
    Long parentId,
    String name,
    String path,
    String component,
    String icon,
    Integer sortOrder,
    Byte type,
    String permissionCode,
    Byte visible,
    Byte status,
    List<RoleMenuResponse> children) {}

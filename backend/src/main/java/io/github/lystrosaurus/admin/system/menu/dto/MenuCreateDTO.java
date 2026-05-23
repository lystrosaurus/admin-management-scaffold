package io.github.lystrosaurus.admin.system.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 菜单创建DTO
 *
 * @param parentId 父菜单ID
 * @param name 菜单名称
 * @param path 路由路径
 * @param component 前端组件路径
 * @param icon 图标
 * @param sortOrder 排序号
 * @param type 类型
 * @param permissionCode 权限码
 * @param visible 可见性
 */
public record MenuCreateDTO(
    Long parentId,
    @NotBlank(message = "菜单名称不能为空") String name,
    String path,
    String component,
    String icon,
    Integer sortOrder,
    @NotNull(message = "菜单类型不能为空") Byte type,
    String permissionCode,
    Byte visible) {}

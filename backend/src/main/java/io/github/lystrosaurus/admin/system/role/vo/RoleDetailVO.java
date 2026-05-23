package io.github.lystrosaurus.admin.system.role.vo;

import io.github.lystrosaurus.admin.system.menu.vo.MenuVO;
import io.github.lystrosaurus.admin.system.permission.vo.PermissionVO;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色详情VO
 *
 * @param id 主键ID
 * @param code 角色编码
 * @param name 角色名称
 * @param description 描述
 * @param sortOrder 排序号
 * @param status 状态
 * @param dataScopeType 数据权限范围
 * @param permissions 权限列表
 * @param menus 菜单列表
 * @param createdAt 创建时间
 */
public record RoleDetailVO(
    Long id,
    String code,
    String name,
    String description,
    Integer sortOrder,
    String status,
    String dataScopeType,
    List<PermissionVO> permissions,
    List<MenuVO> menus,
    LocalDateTime createdAt) {}

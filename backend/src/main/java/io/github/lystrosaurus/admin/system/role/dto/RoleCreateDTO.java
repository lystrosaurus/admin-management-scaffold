package io.github.lystrosaurus.admin.system.role.dto;

/**
 * 角色创建DTO
 *
 * @param code 角色编码
 * @param name 角色名称
 * @param description 描述
 * @param sortOrder 排序号
 * @param dataScopeType 数据权限范围
 */
public record RoleCreateDTO(
    String code, String name, String description, Integer sortOrder, String dataScopeType) {}

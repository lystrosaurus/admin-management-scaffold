package io.github.lystrosaurus.admin.system.role.dto;

import jakarta.validation.constraints.NotBlank;

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
    @NotBlank(message = "角色编码不能为空") String code,
    @NotBlank(message = "角色名称不能为空") String name,
    String description,
    Integer sortOrder,
    String dataScopeType) {}

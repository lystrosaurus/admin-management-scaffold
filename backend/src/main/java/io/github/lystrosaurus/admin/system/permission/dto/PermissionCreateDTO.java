package io.github.lystrosaurus.admin.system.permission.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 权限创建DTO
 *
 * @param code 权限编码
 * @param name 权限名称
 * @param type 类型
 * @param module 所属模块
 * @param resource 资源标识
 * @param action 操作类型
 */
public record PermissionCreateDTO(
    @NotBlank(message = "权限编码不能为空") String code,
    @NotBlank(message = "权限名称不能为空") String name,
    String type,
    String module,
    String resource,
    String action) {}

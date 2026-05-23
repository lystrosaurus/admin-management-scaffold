package io.github.lystrosaurus.admin.system.permission.dto;

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
    String code, String name, String type, String module, String resource, String action) {}

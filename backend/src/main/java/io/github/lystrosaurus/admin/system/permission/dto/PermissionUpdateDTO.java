package io.github.lystrosaurus.admin.system.permission.dto;

/**
 * 权限更新DTO
 *
 * @param name 权限名称
 * @param type 类型
 * @param module 所属模块
 * @param resource 资源标识
 * @param action 操作类型
 * @param status 状态
 */
public record PermissionUpdateDTO(
    String name, String type, String module, String resource, String action, String status) {}

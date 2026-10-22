package io.github.lystrosaurus.admin.system.role.adapter.response;

/**
 * 角色详情中的权限项（字段集合与原 PermissionVO 完全一致）。
 *
 * @param id 主键ID
 * @param code 权限编码
 * @param name 权限名称
 * @param type 类型
 * @param module 所属模块
 * @param resource 资源标识
 * @param action 操作类型
 */
public record RolePermissionResponse(
    Long id, String code, String name, String type, String module, String resource, String action) {}

package io.github.lystrosaurus.admin.system.role.adapter.response;

/**
 * 角色响应（字段集合与原 RoleVO 完全一致，保证 API 契约不变）。
 *
 * @param id 主键ID
 * @param code 角色编码
 * @param name 角色名称
 * @param description 描述
 * @param status 状态
 * @param dataScopeType 数据权限范围
 */
public record RoleResponse(
    Long id, String code, String name, String description, String status, String dataScopeType) {}

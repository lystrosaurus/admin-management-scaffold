package io.github.lystrosaurus.admin.system.role.domain.acl;

/**
 * 角色摘要（跨上下文读模型）。
 *
 * @param id 角色ID
 * @param code 角色编码
 * @param name 角色名称
 * @param description 描述
 * @param status 状态
 * @param dataScopeType 数据权限范围
 */
public record RoleSummary(
    Long id, String code, String name, String description, String status, String dataScopeType) {}

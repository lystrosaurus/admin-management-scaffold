package io.github.lystrosaurus.admin.system.role.vo;

/**
 * 角色VO
 *
 * @param id 主键ID
 * @param code 角色编码
 * @param name 角色名称
 * @param description 描述
 * @param status 状态
 * @param dataScopeType 数据权限范围
 */
public record RoleVO(
    Long id, String code, String name, String description, String status, String dataScopeType) {}

package io.github.lystrosaurus.admin.system.permission.domain.acl;

import java.util.List;

/**
 * 权限摘要（跨上下文读模型）。
 *
 * @param id 权限ID
 * @param code 权限编码
 * @param name 权限名称
 * @param type 类型
 * @param module 所属模块
 * @param resource 资源标识
 * @param action 操作类型
 */
public record PermissionSummary(
    Long id, String code, String name, String type, String module, String resource, String action) {}
